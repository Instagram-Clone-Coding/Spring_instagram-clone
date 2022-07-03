package cloneproject.Instagram.domain.dm.repository.querydsl;

import cloneproject.Instagram.domain.dm.entity.JoinRoom;
import cloneproject.Instagram.domain.dm.entity.Message;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static cloneproject.Instagram.domain.dm.entity.QMessage.message;

@RequiredArgsConstructor
public class MessageRepositoryQuerydslImpl implements MessageRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Message> findAllByJoinRoom(JoinRoom joinRoom, Pageable pageable) {
        final List<Message> messages = queryFactory
            .selectFrom(message)
            .where(isMessageByRoomIdAndAfter(joinRoom.getRoom().getId(), joinRoom.getCreatedDate()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(message.id.desc())
            .fetch();

        final long total = queryFactory
            .selectFrom(message)
            .where(isMessageByRoomIdAndAfter(joinRoom.getRoom().getId(), joinRoom.getCreatedDate()))
            .fetchCount();

        return new PageImpl<>(messages, pageable, total);
    }

    private BooleanExpression isMessageByRoomIdAndAfter(Long roomId, LocalDateTime joinedDate) {
        return message.room.id.eq(roomId).and(message.createdDate.goe(joinedDate));
    }

}
