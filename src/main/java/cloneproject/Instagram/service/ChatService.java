package cloneproject.Instagram.service;

import cloneproject.Instagram.dto.chat.*;
import cloneproject.Instagram.entity.chat.*;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.exception.JoinRoomNotFoundException;
import cloneproject.Instagram.exception.MemberDoesNotExistException;
import cloneproject.Instagram.exception.ChatRoomNotFoundException;
import cloneproject.Instagram.repository.MemberRepository;
import cloneproject.Instagram.repository.chat.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        final long unseenCount = roomUnreadMemberRepository.countByRoomId(room.getId());
        if (roomUnreadMemberRepository.findByRoomIdAndMemberId(room.getId(), member.getId()).isEmpty())
            return new ChatRoomInquireResponse(false, unseenCount);

        final List<JoinRoom> joinRooms = joinRoomRepository.findAllByRoomId(roomId);
        final MessageResponse response = new MessageResponse(MessageAction.MESSAGE_ACK, new MessageSeenDTO(room.getId(), memberId, LocalDateTime.now()));
        joinRooms.forEach(j -> {
            if (!j.getMember().getId().equals(memberId)) {
                messagingTemplate.convertAndSend("/sub/" + j.getMember().getUsername(), response);
            }
        });

        roomUnreadMemberRepository.deleteByRoomIdAndMemberId(room.getId(), member.getId());
        return new ChatRoomInquireResponse(true, unseenCount - 1);
    }

    @Transactional
    public JoinRoomDeleteResponse deleteJoinRoom(Long roomId) {
        final Room room = roomRepository.findById(roomId).orElseThrow(ChatRoomNotFoundException::new);
        final Long memberId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);

        if (joinRoomRepository.findByMemberIdAndRoomId(member.getId(), room.getId()).isEmpty())
            throw new JoinRoomNotFoundException();
        joinRoomRepository.deleteByMemberIdAndRoomId(member.getId(), room.getId());

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

        final MessageText message = messageTextRepository.save(new MessageText(request.getContent(), sender, room));
        room.updateLastMessage(message);

        final List<Member> members = roomMembers.stream()
                .map(RoomMember::getMember)
                .collect(Collectors.toList());
        final Map<Long, RoomUnreadMember> roomUnreadMemberMap = roomUnreadMemberRepository.findByRoomAndMemberIn(room, members).stream()
                .collect(Collectors.toMap(r -> r.getMember().getId(), r -> r));
        final Map<Long, JoinRoom> joinRoomMap = joinRoomRepository.findByRoomAndMemberIn(room, members).stream()
                .collect(Collectors.toMap(j -> j.getMember().getId(), j -> j));

        final List<RoomUnreadMember> newRoomUnreadMembers = new ArrayList<>();
        final List<JoinRoom> newJoinRooms = new ArrayList<>();
        final List<JoinRoom> updateJoinRooms = new ArrayList<>();
        for (RoomMember roomMember : roomMembers) {
            final Member member = roomMember.getMember();
            if (!member.getId().equals(request.getSenderId()) && !roomUnreadMemberMap.containsKey(member.getId()))
                newRoomUnreadMembers.add(new RoomUnreadMember(room, member));
            if (joinRoomMap.containsKey(member.getId()))
                updateJoinRooms.add(joinRoomMap.get(member.getId()));
            else
                newJoinRooms.add(new JoinRoom(room, member));
        }
        roomUnreadMemberRepository.saveAllBatch(newRoomUnreadMembers);
        joinRoomRepository.saveAllBatch(newJoinRooms);
        joinRoomRepository.updateAllBatch(updateJoinRooms);

        final MessageResponse response = new MessageResponse(MessageAction.MESSAGE_GET, new MessageDTO(message));
        roomMembers.forEach(r -> messagingTemplate.convertAndSend("/sub/" + r.getMember().getUsername(), response));
    }

    public void indicate(IndicateRequest request) {
        final Room room = roomRepository.findById(request.getRoomId()).orElseThrow(ChatRoomNotFoundException::new);
        final List<JoinRoom> joinRooms = joinRoomRepository.findAllByRoomId(room.getId());
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
}
