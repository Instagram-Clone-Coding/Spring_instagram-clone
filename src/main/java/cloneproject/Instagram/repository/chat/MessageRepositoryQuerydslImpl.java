package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.dto.chat.MessageDTO;
import cloneproject.Instagram.dto.chat.QMessageDTO;
import cloneproject.Instagram.entity.chat.JoinRoom;
import cloneproject.Instagram.exception.JoinRoomNotFoundException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static cloneproject.Instagram.entity.chat.QJoinRoom.joinRoom;
import static cloneproject.Instagram.entity.chat.QMessage.message;
import static cloneproject.Instagram.entity.chat.QRoom.room;
import static cloneproject.Instagram.entity.member.QMember.member;

@RequiredArgsConstructor
public class MessageRepositoryQuerydslImpl implements MessageRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MessageDTO> findMessageDTOPageByMemberIdAndRoomId(Long memberId, Long roomId, Pageable pageable) {
        final JoinRoom findJoinRoom = queryFactory
                .selectFrom(joinRoom)
                .where(joinRoom.member.id.eq(memberId).and(joinRoom.room.id.eq(roomId)))
                .innerJoin(joinRoom.room, room)
                .innerJoin(joinRoom.member, member)
                .fetchOne();

        if (findJoinRoom == null)
            throw new JoinRoomNotFoundException();

        final List<MessageDTO> messageDTOs = queryFactory
                .select(new QMessageDTO(message))
                .from(message)
                .innerJoin(message.member, member)
                .innerJoin(message.room, room)
                .where(
                        message.member.id.eq(findJoinRoom.getMember().getId()).and(
                                message.room.id.eq(findJoinRoom.getRoom().getId()).and(
                                        message.createdDate.after(findJoinRoom.getCreatedDate())
                                )
                        ))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(message.createdDate.desc())
                .fetch();

        return new PageImpl<>(messageDTOs, pageable, messageDTOs.size());
    }
}
