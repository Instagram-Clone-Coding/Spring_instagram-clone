package cloneproject.Instagram.domain.dm.repository.querydsl;

import static cloneproject.Instagram.domain.dm.entity.QJoinRoom.*;
import static cloneproject.Instagram.domain.dm.entity.QMessage.*;
import static cloneproject.Instagram.domain.dm.entity.QRoom.*;
import static cloneproject.Instagram.domain.dm.entity.QRoomUnreadMember.*;
import static cloneproject.Instagram.domain.member.entity.QMember.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import cloneproject.Instagram.domain.dm.dto.JoinRoomDto;
import cloneproject.Instagram.domain.dm.dto.QJoinRoomDto;
import cloneproject.Instagram.domain.dm.entity.JoinRoom;

@RequiredArgsConstructor
public class JoinRoomRepositoryQuerydslImpl implements JoinRoomRepositoryQuerydsl {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<JoinRoom> findWithRoomAndMemberByMemberIdAndRoomId(Long memberId, Long roomId) {
		return Optional.ofNullable(queryFactory
			.selectFrom(joinRoom)
			.where(joinRoom.member.id.eq(memberId).and(joinRoom.room.id.eq(roomId)))
			.innerJoin(joinRoom.room, room).fetchJoin()
			.innerJoin(joinRoom.member, member).fetchJoin()
			.fetchOne());
	}

	@Override
	public List<JoinRoom> findAllWithMessageByMemberIdAndRoomIdIn(Long memberId, List<Long> roomIds) {
		return queryFactory
			.selectFrom(joinRoom)
			.innerJoin(joinRoom.message, message)
			.where(joinRoom.member.id.eq(memberId).and(
				joinRoom.room.id.in(roomIds)
			))
			.fetch();
	}

	@Override
	public Page<JoinRoomDto> findJoinRoomDtoPageByMemberId(Long memberId, Pageable pageable) {
		final List<JoinRoomDto> joinRoomDtos = queryFactory
			.select(new QJoinRoomDto(
				joinRoom.room.id,
				isUnread(memberId),
				joinRoom.room.member
			))
			.from(joinRoom)
			.innerJoin(joinRoom.room, room)
			.innerJoin(joinRoom.message, message)
			.innerJoin(joinRoom.room.member, member)
			.where(joinRoom.member.id.eq(memberId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(joinRoom.message.createdDate.desc())
			.fetch();

		final long total = queryFactory
			.selectFrom(joinRoom)
			.where(joinRoom.member.id.eq(memberId))
			.fetchCount();

		return new PageImpl<>(joinRoomDtos, pageable, total);
	}

	private BooleanExpression isUnread(Long memberId) {
		return JPAExpressions
			.selectFrom(roomUnreadMember)
			.where(
				roomUnreadMember.member.id.eq(memberId).and(
					roomUnreadMember.room.eq(joinRoom.room)
				)
			)
			.exists();
	}

}
