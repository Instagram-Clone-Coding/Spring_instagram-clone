package cloneproject.Instagram.service;

import cloneproject.Instagram.dto.StatusResponse;
import cloneproject.Instagram.dto.chat.*;
import cloneproject.Instagram.dto.error.ErrorCode;
import cloneproject.Instagram.dto.error.ErrorResponse;
import cloneproject.Instagram.entity.chat.*;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.exception.*;
import cloneproject.Instagram.repository.MemberRepository;
import cloneproject.Instagram.repository.chat.*;
import cloneproject.Instagram.util.S3Uploader;
import cloneproject.Instagram.vo.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final RoomUnreadMemberRepository roomUnreadMemberRepository;
    private final MessageRepository messageRepository;
    private final MessageLikeRepository messageLikeRepository;
    private final MessageImageRepository messageImageRepository;
    private final MemberRepository memberRepository;
    private final JoinRoomRepository joinRoomRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageTextRepository messageTextRepository;
    private final S3Uploader uploader;

    @Transactional
    public ChatRoomCreateResponse createRoom(List<String> usernames) {
        final Long memberId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member inviter = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);
        usernames.add(inviter.getUsername());
        final List<Member> members = memberRepository.findAllByUsernameIn(usernames);

        boolean status = false;
        Room room = getRoomByMembers(members);
        if (room == null) {
            status = true;
            room = roomRepository.save(new Room(inviter));
            roomMemberRepository.saveAllBatch(room, members);
        }

        final List<MemberSimpleInfo> memberSimpleInfos = members.stream()
                .map(MemberSimpleInfo::new)
                .collect(Collectors.toList());
        return new ChatRoomCreateResponse(status, room.getId(), new MemberSimpleInfo(inviter), memberSimpleInfos);
    }

    private Room getRoomByMembers(List<Member> members) {
        Room room = null;
        final Map<Long, List<RoomMember>> roomMembersMap = roomMemberRepository.findAllByMemberIn(members).stream()
                .collect(Collectors.groupingBy(r -> r.getRoom().getId()));

        final List<Long> roomIds = new ArrayList<>();
        roomMembersMap.forEach((rid, rms) -> {
            if (rms.size() == members.size())
                roomIds.add(rid);
        });
        final Map<Long, List<RoomMember>> roomMemberMapGroupByRoomId = roomMemberRepository.findAllByRoomIdIn(roomIds).stream()
                .collect(Collectors.groupingBy(r -> r.getRoom().getId()));

        for (Long roomId : roomMemberMapGroupByRoomId.keySet()) {
            if (roomMemberMapGroupByRoomId.get(roomId).size() == members.size()) {
                room = roomMemberMapGroupByRoomId.get(roomId).get(0).getRoom();
                break;
            }
        }
        return room;
    }

    @Transactional
    public ChatRoomInquireResponse inquireRoom(Long roomId) {
        final Room room = roomRepository.findById(roomId).orElseThrow(ChatRoomNotFoundException::new);
        final Long memberId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);

        final Map<Long, List<RoomUnreadMember>> roomUnreadMemberMap = roomUnreadMemberRepository.findAllByRoom(room).stream()
                .collect(Collectors.groupingBy(r -> r.getMember().getId()));
        long unseenCount = 0;
        for (Long id : roomUnreadMemberMap.keySet()) {
            if (roomUnreadMemberMap.get(id).isEmpty())
                continue;
            unseenCount++;
        }
        if (roomUnreadMemberRepository.findAllByRoomAndMember(room, member).isEmpty())
            return new ChatRoomInquireResponse(false, unseenCount);

        final List<JoinRoom> joinRooms = joinRoomRepository.findAllWithMemberByRoomId(roomId);
        final MessageResponse response = new MessageResponse(MessageAction.MESSAGE_SEEN, new MessageSeenDTO(room.getId(), memberId, LocalDateTime.now()));
        joinRooms.forEach(j -> {
            if (!j.getMember().getId().equals(memberId)) {
                messagingTemplate.convertAndSend("/sub/" + j.getMember().getUsername(), response);
            }
        });

        final List<RoomUnreadMember> roomUnreadMembers = roomUnreadMemberRepository.findAllByRoomAndMember(room, member);
        roomUnreadMemberRepository.deleteAllInBatch(roomUnreadMembers);
        return new ChatRoomInquireResponse(true, unseenCount - 1);
    }

    @Transactional
    public JoinRoomDeleteResponse deleteJoinRoom(Long roomId) {
        final Room room = roomRepository.findById(roomId).orElseThrow(ChatRoomNotFoundException::new);
        final Long memberId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);

        if (joinRoomRepository.findByMemberAndRoom(member, room).isEmpty())
            throw new JoinRoomNotFoundException();
        joinRoomRepository.deleteByMemberAndRoom(member, room);

        return new JoinRoomDeleteResponse(true);
    }

    public Page<JoinRoomDTO> getJoinRooms(int page) {
        final Long memberId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);

        page = (page == 0 ? 0 : page - 1);
        Pageable pageable = PageRequest.of(page, 10, Sort.by(DESC, "id"));
        return joinRoomRepository.findJoinRoomDTOPageByMemberId(member.getId(), pageable);
    }

    @Transactional
    public void sendMessage(MessageRequest request) {
        final Member sender = memberRepository.findById(request.getSenderId()).orElseThrow(MemberDoesNotExistException::new);
        final Room room = roomRepository.findById(request.getRoomId()).orElseThrow(ChatRoomNotFoundException::new);
        final List<RoomMember> roomMembers = roomMemberRepository.findAllWithMemberByRoomId(room.getId());
        if (roomMembers.stream().noneMatch(r -> r.getMember().getId().equals(sender.getId())))
            throw new JoinRoomNotFoundException();

        final MessageText message = messageTextRepository.save(new MessageText(request.getContent(), sender, room));
        message.setDtype();
        updateRoom(request.getSenderId(), room, roomMembers, message);

        final MessageResponse response = new MessageResponse(MessageAction.MESSAGE_GET, new MessageDTO(message));
        roomMembers.forEach(r -> messagingTemplate.convertAndSend("/sub/" + r.getMember().getUsername(), response));
    }

    public void indicate(IndicateRequest request) {
        final Room room = roomRepository.findById(request.getRoomId()).orElseThrow(ChatRoomNotFoundException::new);
        final List<JoinRoom> joinRooms = joinRoomRepository.findAllWithMemberByRoomId(room.getId());
        final MessageResponse response = new MessageResponse(MessageAction.MESSAGE_ACK, new IndicateDTO(room.getId(), request.getSenderId(), 12000L));

        joinRooms.forEach(j -> {
            if (!j.getMember().getId().equals(request.getSenderId())) {
                messagingTemplate.convertAndSend("/sub/" + j.getMember().getUsername(), response);
            }
        });
    }

    public Page<MessageDTO> getChatMessages(Long roomId, Integer page) {
        final Long memberId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        page = (page == 0 ? 0 : page - 1);
        Pageable pageable = PageRequest.of(page, 10);
        return messageRepository.findMessageDTOPageByMemberIdAndRoomId(memberId, roomId, pageable);
    }

    @Transactional
    public StatusResponse sendImage(Long roomId, MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            final List<ErrorResponse.FieldError> errors = new ArrayList<>();
            errors.add(new ErrorResponse.FieldError("image", "null", ErrorCode.MESSAGE_IMAGE_INVALID.getMessage()));
            throw new InvalidInputException(errors);
        }

        final Long senderId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member sender = memberRepository.findById(senderId).orElseThrow(MemberDoesNotExistException::new);
        final Room room = roomRepository.findById(roomId).orElseThrow(ChatRoomNotFoundException::new);
        final List<RoomMember> roomMembers = roomMemberRepository.findAllWithMemberByRoomId(room.getId());
        if (roomMembers.stream().noneMatch(r -> r.getMember().getId().equals(sender.getId())))
            throw new JoinRoomNotFoundException();

        final Image image = uploader.uploadImage(multipartFile, "chat");
        final MessageImage message = messageImageRepository.save(new MessageImage(image, sender, room));
        message.setDtype();
        updateRoom(senderId, room, roomMembers, message);

        final MessageResponse response = new MessageResponse(MessageAction.MESSAGE_GET, new MessageDTO(message));
        roomMembers.forEach(r -> messagingTemplate.convertAndSend("/sub/" + r.getMember().getUsername(), response));
        return new StatusResponse(true);
    }

    private void updateRoom(Long senderId, Room room, List<RoomMember> roomMembers, Message message) {
        final List<Member> members = roomMembers.stream()
                .map(RoomMember::getMember)
                .collect(Collectors.toList());
        final Map<Long, JoinRoom> joinRoomMap = joinRoomRepository.findByRoomAndMemberIn(room, members).stream()
                .collect(Collectors.toMap(j -> j.getMember().getId(), j -> j));

        final List<RoomUnreadMember> newRoomUnreadMembers = new ArrayList<>();
        final List<JoinRoom> newJoinRooms = new ArrayList<>();
        final List<JoinRoom> updateJoinRooms = new ArrayList<>();
        for (RoomMember roomMember : roomMembers) {
            final Member member = roomMember.getMember();
            if (!member.getId().equals(senderId))
                newRoomUnreadMembers.add(new RoomUnreadMember(room, message, member));
            if (joinRoomMap.containsKey(member.getId()))
                updateJoinRooms.add(joinRoomMap.get(member.getId()));
            else
                newJoinRooms.add(new JoinRoom(room, member, message));
        }
        roomUnreadMemberRepository.saveAllBatch(newRoomUnreadMembers, message);
        joinRoomRepository.saveAllBatch(newJoinRooms, message);
        joinRoomRepository.updateAllBatch(updateJoinRooms, message);
    }

    @Transactional
    public void deleteMessage(Long messageId, Long memberId) {
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);
        final Message message = messageRepository.findWithRoomById(messageId).orElseThrow(MessageNotFoundException::new);
        if (!message.getMember().getId().equals(member.getId())) {
            final List<ErrorResponse.FieldError> errors = new ArrayList<>();
            errors.add(new ErrorResponse.FieldError("memberId", memberId.toString(), ErrorCode.MESSAGE_SENDER_MISMATCH.getMessage()));
            throw new InvalidInputException(errors);
        }

        final List<RoomUnreadMember> roomUnreadMembers = roomUnreadMemberRepository.findAllByMessage(message);
        roomUnreadMemberRepository.deleteAllInBatch(roomUnreadMembers);

        final Room room = message.getRoom();
        final List<JoinRoom> joinRooms = joinRoomRepository.findAllWithMessageByRoomId(room.getId());
        joinRooms.forEach(joinRoom -> {
            final LocalDateTime createdDateOfMessageToDelete = message.getCreatedDate();
            final LocalDateTime createdDateOfJoinRoom = joinRoom.getCreatedDate();
            if (!createdDateOfMessageToDelete.isBefore(createdDateOfJoinRoom)) {
                if (message.equals(joinRoom.getMessage())) {
                    final LocalDateTime start = createdDateOfJoinRoom.minusSeconds(1L);
                    final LocalDateTime end = createdDateOfMessageToDelete.plusSeconds(1L);
                    final Long total = messageRepository.countByCreatedDateBetweenAndRoom(start, end, room);
                    if (total == 1) {
                        joinRoomRepository.delete(joinRoom);
                    } else {
                        final List<Message> messages = messageRepository.findTop2ByCreatedDateBetweenAndRoomOrderByIdDesc(start, end, room);
                        joinRoom.updateMessage(messages.get(1));
                    }
                }
            }
        });

        final MessageResponse response = new MessageResponse(MessageAction.MESSAGE_DELETE, new MessageSimpleDTO(message, member));
        messageRepository.delete(message);
        final List<RoomMember> roomMembers = roomMemberRepository.findAllByRoom(room);
        roomMembers.forEach(r -> messagingTemplate.convertAndSend("/sub/" + r.getMember().getUsername(), response));
    }

    @Transactional
    public void likeMessage(Long messageId, Long memberId) {
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);
        final Message message = messageRepository.findWithRoomById(messageId).orElseThrow(MessageNotFoundException::new);
        final List<RoomMember> roomMembers = roomMemberRepository.findAllWithMemberByRoomId(message.getRoom().getId());
        if (roomMembers.stream().noneMatch(r -> r.getMember().getId().equals(member.getId())))
            throw new JoinRoomNotFoundException();

        if (messageLikeRepository.findByMemberAndMessage(member, message).isPresent())
            throw new MessageLikeAlreadyExistException();
        messageLikeRepository.save(new MessageLike(member, message));

        final MessageResponse response = new MessageResponse(MessageAction.MESSAGE_LIKE, new MessageSimpleDTO(message, member));
        roomMembers.forEach(r -> messagingTemplate.convertAndSend("/sub/" + r.getMember().getUsername(), response));
    }

    @Transactional
    public void unlikeMessage(Long messageId, Long memberId) {
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);
        final Message message = messageRepository.findWithRoomById(messageId).orElseThrow(MessageNotFoundException::new);
        final List<RoomMember> roomMembers = roomMemberRepository.findAllWithMemberByRoomId(message.getRoom().getId());
        if (roomMembers.stream().noneMatch(r -> r.getMember().getId().equals(member.getId())))
            throw new JoinRoomNotFoundException();

        final Optional<MessageLike> findMessageLike = messageLikeRepository.findByMemberAndMessage(member, message);
        if (findMessageLike.isEmpty())
            throw new MessageLikeNotFoundException();
        messageLikeRepository.delete(findMessageLike.get());

        final MessageResponse response = new MessageResponse(MessageAction.MESSAGE_UNLIKE, new MessageSimpleDTO(message, member));
        roomMembers.forEach(r -> messagingTemplate.convertAndSend("/sub/" + r.getMember().getUsername(), response));
    }
}
