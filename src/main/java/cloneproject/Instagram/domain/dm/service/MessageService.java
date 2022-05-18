package cloneproject.Instagram.domain.dm.service;

import cloneproject.Instagram.domain.dm.dto.MessageAction;
import cloneproject.Instagram.domain.dm.dto.MessageDto;
import cloneproject.Instagram.domain.dm.dto.MessageResponse;
import cloneproject.Instagram.domain.dm.entity.*;
import cloneproject.Instagram.domain.dm.repository.*;
import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final MessagePostRepository messagePostRepository;
    private final MemberRepository memberRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final RoomRepository roomRepository;
    private final RoomUnreadMemberRepository roomUnreadMemberRepository;
    private final JoinRoomRepository joinRoomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void deleteMessagePosts(Post post) {
        final List<MessagePost> messagePosts = messagePostRepository.findAllByPost(post);
        messagePosts.forEach(MessagePost::deletePost);
    }

    @Transactional
    public void sendMessageToMembersIndividually(Member sender, Post post, Collection<String> usernames) {
        final List<Member> receivers = memberRepository.findAllByUsernameIn(usernames);
        final List<Member> members = new ArrayList<>(receivers);
        members.add(sender);

        final List<RoomMember> roomMembers = roomMemberRepository.findAllByMemberIn(members);
        final Map<Member, List<RoomMember>> roomMemberMapGroupByMember = roomMembers.stream()
                .collect(Collectors.groupingBy(RoomMember::getMember));
        final Map<Room, List<RoomMember>> roomMemberMapGroupByRoom = roomMembers.stream()
                .collect(Collectors.groupingBy(RoomMember::getRoom));

        receivers.forEach(receiver -> {
            final Room room = getPrivateRoom(sender, roomMemberMapGroupByMember, roomMemberMapGroupByRoom, receiver);
            final List<Member> privateRoomMembers = List.of(sender, receiver);
            final MessagePost newMessage = messagePostRepository.save(new MessagePost(post, sender, room));
            newMessage.setDtype();

            for (Member member : privateRoomMembers) {
                joinRoomRepository.findByMemberAndRoom(member, room)
                        .ifPresentOrElse(joinRoom -> joinRoom.updateMessage(newMessage),
                                () -> joinRoomRepository.save(new JoinRoom(room, member, newMessage)));
            }
            roomUnreadMemberRepository.save(new RoomUnreadMember(room, newMessage, receiver));

            final MessageResponse response = new MessageResponse(MessageAction.MESSAGE_GET, new MessageDto(newMessage));
            messagingTemplate.convertAndSend("/sub/" + receiver.getUsername(), response);
        });
    }

    private Room getPrivateRoom(Member sender, Map<Member, List<RoomMember>> roomMemberMapGroupByMember, Map<Room, List<RoomMember>> roomMemberMapGroupByRoom, Member receiver) {
        if (roomMemberMapGroupByMember.containsKey(receiver)) {
            for (RoomMember roomMemberOfReceiver : roomMemberMapGroupByMember.get(receiver)) {
                if (!roomMemberMapGroupByMember.containsKey(sender))
                    continue;

                final Long roomIdOfReceiver = roomMemberOfReceiver.getRoom().getId();
                for (RoomMember roomMemberOfSender : roomMemberMapGroupByMember.get(sender)) {
                    final Long roomIdOfSender = roomMemberOfSender.getRoom().getId();
                    final boolean isPrivateRoom = roomMemberMapGroupByRoom.get(roomMemberOfSender.getRoom()).size() == 2;

                    if (roomIdOfReceiver.equals(roomIdOfSender) && isPrivateRoom)
                        return roomMemberOfSender.getRoom();
                }
            }
        }

        final Room room = roomRepository.save(new Room(sender));
        roomMemberRepository.saveAll(List.of(new RoomMember(sender, room), new RoomMember(receiver, room)));
        return room;
    }

}
