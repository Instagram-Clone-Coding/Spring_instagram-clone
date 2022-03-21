package cloneproject.Instagram.service;

import cloneproject.Instagram.dto.StatusResponse;
import cloneproject.Instagram.dto.chat.MessageAction;
import cloneproject.Instagram.dto.chat.MessageDTO;
import cloneproject.Instagram.dto.chat.MessageResponse;
import cloneproject.Instagram.dto.error.ErrorResponse;
import cloneproject.Instagram.dto.story.StoryContentRequest;
import cloneproject.Instagram.dto.story.StoryUploadRequest;
import cloneproject.Instagram.entity.chat.*;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.entity.member.MemberStory;
import cloneproject.Instagram.entity.story.Story;
import cloneproject.Instagram.exception.InvalidInputException;
import cloneproject.Instagram.exception.MemberDoesNotExistException;
import cloneproject.Instagram.repository.MemberRepository;
import cloneproject.Instagram.repository.chat.JoinRoomRepository;
import cloneproject.Instagram.repository.chat.RoomMemberRepository;
import cloneproject.Instagram.repository.chat.RoomRepository;
import cloneproject.Instagram.repository.chat.RoomUnreadMemberRepository;
import cloneproject.Instagram.repository.story.MemberStoryRedisRepository;
import cloneproject.Instagram.repository.story.MessageStoryRepository;
import cloneproject.Instagram.repository.story.StoryRepository;
import cloneproject.Instagram.util.AuthUtil;
import cloneproject.Instagram.util.S3Uploader;
import cloneproject.Instagram.util.StringExtractUtil;
import cloneproject.Instagram.vo.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static cloneproject.Instagram.dto.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoryService {

    private final MemberStoryRedisRepository memberStoryRedisRepository;
    private final StoryRepository storyRepository;
    private final MemberRepository memberRepository;
    private final S3Uploader s3Uploader;
    private final StringExtractUtil stringExtractUtil;
    private final SimpMessagingTemplate messagingTemplate;
    private final RoomMemberRepository roomMemberRepository;
    private final RoomRepository roomRepository;
    private final MessageStoryRepository messageStoryRepository;
    private final RoomUnreadMemberRepository roomUnreadMemberRepository;
    private final JoinRoomRepository joinRoomRepository;

    @Transactional
    public StatusResponse upload(StoryUploadRequest request) {
        final List<MultipartFile> storyImages = request.getStoryImages();
        final Map<Integer, List<StoryContentRequest>> storyContentMap = request.getStoryContents().stream()
                .collect(Collectors.groupingBy(StoryContentRequest::getId));
        validateStoryUploadRequest(storyImages, storyContentMap);

        final Long memberId = AuthUtil.getLoginedMemberIdOrNull();
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);

        final Set<String> usernames = new HashSet<>();
        final List<Long> storyIds = new ArrayList<>();
        for (int i = 0; i < storyImages.size(); i++) {
            final Image image = s3Uploader.uploadImage(storyImages.get(i), "story");
            final Story story = storyRepository.save(new Story(member, image));
            storyIds.add(story.getId());

            if (!storyContentMap.containsKey(i + 1))
                continue;
            final List<StoryContentRequest> storyContents = storyContentMap.get(i + 1);
            for (StoryContentRequest storyContent : storyContents) {
                final List<String> mentionedUsernames = stringExtractUtil.extractMentions(storyContent.getContent(), List.of(member.getUsername()));
                usernames.addAll(mentionedUsernames);
            }

            final List<Member> members = memberRepository.findAllByUsernameIn(usernames);
            members.add(member);
            final List<RoomMember> roomMembers = roomMemberRepository.findAllByMemberIn(members);
            final Map<Member, List<RoomMember>> roomMemberMapGroupByMember = roomMembers.stream()
                    .collect(Collectors.groupingBy(RoomMember::getMember));
            final Map<Room, List<RoomMember>> roomMemberMapGroupByRoom = roomMembers.stream()
                    .collect(Collectors.groupingBy(RoomMember::getRoom));

            members.remove(member);
            members.forEach(mentionedMember -> {
                Room room = null;

                if (roomMemberMapGroupByMember.containsKey(mentionedMember)) {
                    for (RoomMember taggedRoomMember : roomMemberMapGroupByMember.get(mentionedMember)) {
                        if (!roomMemberMapGroupByMember.containsKey(member))
                            continue;

                        for (RoomMember roomMember : roomMemberMapGroupByMember.get(member)) {
                            if (taggedRoomMember.getRoom().getId().equals(roomMember.getRoom().getId())
                                    && roomMemberMapGroupByRoom.get(roomMember.getRoom()).size() == 2) {
                                room = roomMember.getRoom();
                                break;
                            }
                        }
                        if (room != null)
                            break;
                    }
                }

                if (room == null) {
                    room = roomRepository.save(new Room(member));
                    roomMemberRepository.save(new RoomMember(member, room));
                    roomMemberRepository.save(new RoomMember(mentionedMember, room));
                }

                final MessageStory message = messageStoryRepository.save(new MessageStory(story, member, room));
                message.setDtype();
                roomUnreadMemberRepository.save(new RoomUnreadMember((room), message, mentionedMember));

                final List<Member> privateRoomMembers = List.of(member, mentionedMember);
                for (Member privateRoomMember : privateRoomMembers) {
                    final Optional<JoinRoom> joinRoom = joinRoomRepository.findByMemberAndRoom(privateRoomMember, room);
                    if (joinRoom.isPresent())
                        joinRoom.get().updateMessage(message);
                    else
                        joinRoomRepository.save(new JoinRoom(room, privateRoomMember, message));
                }

                final MessageResponse response = new MessageResponse(MessageAction.MESSAGE_GET, new MessageDTO(message));
                messagingTemplate.convertAndSend("/sub/" + mentionedMember.getUsername(), response);
            });

            usernames.clear();
        }

        memberStoryRedisRepository.deleteById(member.getId());
        storyIds.forEach(storyId -> memberStoryRedisRepository.save(new MemberStory(member.getId(), storyId)));

        return new StatusResponse(true);
    }

    private void validateStoryUploadRequest(List<MultipartFile> storyImages, Map<Integer, List<StoryContentRequest>> storyContentMap) {
        final List<ErrorResponse.FieldError> errors = new ArrayList<>();
        if (storyImages.isEmpty()) {
            errors.add(new ErrorResponse.FieldError("storyImages", "empty", INVALID_STORY_IMAGE.getMessage()));
            throw new InvalidInputException(errors);
        }

        storyContentMap.keySet().forEach(id -> {
            if (id > storyImages.size() || id < 1) {
                errors.add(new ErrorResponse.FieldError("storyContents.id", id.toString(), INVALID_STORY_IMAGE_INDEX.getMessage()));
                throw new InvalidInputException(errors);
            }
        });
    }
}
