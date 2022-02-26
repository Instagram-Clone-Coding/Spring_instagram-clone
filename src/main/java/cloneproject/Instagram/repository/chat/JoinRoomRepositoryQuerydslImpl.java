package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.dto.chat.JoinRoomDTO;
import cloneproject.Instagram.dto.chat.MemberSimpleInfo;
import cloneproject.Instagram.dto.chat.MessageDTO;
import cloneproject.Instagram.dto.chat.QJoinRoomDTO;
import cloneproject.Instagram.entity.chat.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cloneproject.Instagram.entity.chat.QJoinRoom.joinRoom;
import static cloneproject.Instagram.entity.chat.QMessage.message;
import static cloneproject.Instagram.entity.chat.QMessageImage.messageImage;
import static cloneproject.Instagram.entity.chat.QMessagePost.messagePost;
import static cloneproject.Instagram.entity.chat.QMessageText.messageText;
import static cloneproject.Instagram.entity.chat.QRoom.room;
import static cloneproject.Instagram.entity.chat.QRoomMember.roomMember;
import static cloneproject.Instagram.entity.chat.QRoomUnreadMember.roomUnreadMember;
import static cloneproject.Instagram.entity.member.QMember.member;
import static cloneproject.Instagram.entity.post.QPost.post;
import static com.querydsl.core.group.GroupBy.groupBy;

@RequiredArgsConstructor
public class JoinRoomRepositoryQuerydslImpl implements JoinRoomRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<JoinRoomDTO> findJoinRoomDTOPageByMemberId(Long memberId, Pageable pageable) {
        final List<JoinRoomDTO> joinRoomDTOs = queryFactory
                .select(new QJoinRoomDTO(
                        joinRoom.room.id,
                        JPAExpressions
                                .selectFrom(roomUnreadMember)
                                .where(
                                        roomUnreadMember.member.id.eq(memberId).and(
                                                roomUnreadMember.room.eq(joinRoom.room)
                                        )
                                )
                                .exists(),
                        joinRoom.room.member
                ))
                .from(joinRoom)
                .innerJoin(joinRoom.room, room)
                .innerJoin(joinRoom.room.message, message)
                .innerJoin(joinRoom.room.member, member)
                .where(joinRoom.member.id.eq(memberId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(joinRoom.lastMessageDate.desc())
                .fetch();

        final List<Long> roomIds = joinRoomDTOs.stream()
                .map(JoinRoomDTO::getRoomId)
                .collect(Collectors.toList());
        final List<Message> messages = queryFactory
                .select(room.message)
                .from(room)
                .innerJoin(room.message, message)
                .where(room.id.in(roomIds))
                .fetch();
        final List<Long> messageIds = messages.stream()
                .map(Message::getId)
                .collect(Collectors.toList());

        final Map<Long, MessagePost> messagePostMap = queryFactory
                .from(messagePost)
                .innerJoin(messagePost.post, post).fetchJoin()
                .where(messagePost.id.in(messageIds))
                .transform(groupBy(messagePost.room.id).as(messagePost));

        final Map<Long, MessageImage> messageImageMap = queryFactory
                .from(messageImage)
                .where(messageImage.id.in(messageIds))
                .transform(groupBy(messageImage.room.id).as(messageImage));

        final Map<Long, MessageText> messageTextMap = queryFactory
                .from(messageText)
                .where(messageText.id.in(messageIds))
                .transform(groupBy(messageText.room.id).as(messageText));

        final long total = queryFactory
                .selectFrom(joinRoom)
                .where(joinRoom.member.id.eq(memberId))
                .fetchCount();

        final List<RoomMember> roomMembers = queryFactory
                .selectFrom(roomMember)
                .where(roomMember.room.id.in(roomIds))
                .join(roomMember.member, member).fetchJoin()
                .fetch();
        final Map<Long, List<RoomMember>> roomMemberMap = roomMembers.stream()
                .collect(Collectors.groupingBy(r -> r.getRoom().getId()));

        joinRoomDTOs.forEach(j -> {
            j.setMembers(
                    roomMemberMap.get(j.getRoomId()).stream()
                            .map(r -> new MemberSimpleInfo(r.getMember()))
                            .collect(Collectors.toList())
            );
            if (messagePostMap.containsKey(j.getRoomId()))
                j.setLastMessage(new MessageDTO(messagePostMap.get(j.getRoomId())));
            else if (messageImageMap.containsKey(j.getRoomId()))
                j.setLastMessage(new MessageDTO(messageImageMap.get(j.getRoomId())));
            else if (messageTextMap.containsKey(j.getRoomId()))
                j.setLastMessage(new MessageDTO(messageTextMap.get(j.getRoomId())));
        });

        return new PageImpl<>(joinRoomDTOs, pageable, total);
    }
}
