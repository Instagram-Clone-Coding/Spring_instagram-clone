package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.dto.chat.JoinRoomDTO;
import cloneproject.Instagram.dto.chat.MemberSimpleInfo;
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
import static cloneproject.Instagram.entity.chat.QRoom.room;
import static cloneproject.Instagram.entity.chat.QRoomMember.roomMember;
import static cloneproject.Instagram.entity.chat.QRoomUnreadMember.roomUnreadMember;
import static cloneproject.Instagram.entity.member.QMember.member;

@RequiredArgsConstructor
public class JoinRoomRepositoryQuerydslImpl implements JoinRoomRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<JoinRoomDTO> findJoinRoomDTOPagebyMemberId(Long memberId, Pageable pageable) {
        // TODO: WebSocket 이용 메시지 송/수신하는 시점부터 쿼리 확인 필요
        final List<JoinRoomDTO> joinRoomDTOs = queryFactory
                .select(new QJoinRoomDTO(
                        joinRoom.room.id,
                        joinRoom.room.message,
                        JPAExpressions
                                .selectFrom(roomUnreadMember)
                                .where(roomUnreadMember.member.id.eq(memberId))
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
                .orderBy(joinRoom.lastMessageDate.desc()) // TODO: DB에 메시지 저장하는 시점부터 테스트 필요
                .fetch();

        final List<Long> roomIds = joinRoomDTOs.stream()
                .map(JoinRoomDTO::getRoomId)
                .collect(Collectors.toList());

        final List<RoomMember> roomMembers = queryFactory
                .selectFrom(roomMember)
                .where(roomMember.room.id.in(roomIds))
                .join(roomMember.member, member).fetchJoin()
                .fetch();
        final Map<Long, List<RoomMember>> roomMemberMap = roomMembers.stream()
                .collect(Collectors.groupingBy(r -> r.getRoom().getId()));

        joinRoomDTOs.forEach(j -> j.setInvitees(
                roomMemberMap.get(j.getRoomId())
                        .stream()
                        .map(r -> new MemberSimpleInfo(r.getMember()))
                        .collect(Collectors.toList())));
        // TODO: lastMessage 주입

        return new PageImpl<>(joinRoomDTOs, pageable, joinRoomDTOs.size());
    }
}
