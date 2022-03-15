package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.dto.chat.MessageDTO;
import cloneproject.Instagram.dto.member.MemberDTO;
import cloneproject.Instagram.entity.chat.*;
import cloneproject.Instagram.exception.JoinRoomNotFoundException;
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
import static cloneproject.Instagram.entity.chat.QMessageLike.messageLike;
import static cloneproject.Instagram.entity.chat.QMessagePost.messagePost;
import static cloneproject.Instagram.entity.chat.QMessageStory.messageStory;
import static cloneproject.Instagram.entity.chat.QMessageText.messageText;
import static cloneproject.Instagram.entity.chat.QRoom.room;
import static cloneproject.Instagram.entity.member.QMember.member;
import static cloneproject.Instagram.entity.post.QPost.post;
import static cloneproject.Instagram.entity.story.QStory.story;
import static com.querydsl.core.group.GroupBy.*;

@RequiredArgsConstructor
public class MessageRepositoryQuerydslImpl implements MessageRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MessageDTO> findMessageDTOPageByMemberIdAndRoomId(Long memberId, Long roomId, Pageable pageable) {
        final JoinRoom findJoinRoom = queryFactory
                .selectFrom(joinRoom)
                .where(joinRoom.member.id.eq(memberId).and(joinRoom.room.id.eq(roomId)))
                .innerJoin(joinRoom.room, room).fetchJoin()
                .innerJoin(joinRoom.member, member).fetchJoin()
                .fetchOne();

        if (findJoinRoom == null)
            throw new JoinRoomNotFoundException();

        final List<Message> messages = queryFactory
                .selectFrom(message)
                .where(
                        message.room.id.eq(findJoinRoom.getRoom().getId()).and(
                                message.createdDate.goe(findJoinRoom.getCreatedDate())
                        )
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(message.id.desc())
                .fetch();
        final List<Long> messageIds = messages.stream()
                .map(Message::getId)
                .collect(Collectors.toList());

        final Map<Long, MessageStory> messageStoryMap = queryFactory
                .from(messageStory)
                .leftJoin(messageStory.story, story).fetchJoin()
                .where(messageStory.id.in(messageIds))
                .transform(groupBy(messageStory.id).as(messageStory));

        final Map<Long, MessagePost> messagePostMap = queryFactory
                .from(messagePost)
                .leftJoin(messagePost.post, post).fetchJoin()
                .where(messagePost.id.in(messageIds))
                .transform(groupBy(messagePost.id).as(messagePost));

        final Map<Long, MessageImage> messageImageMap = queryFactory
                .from(messageImage)
                .where(messageImage.id.in(messageIds))
                .transform(groupBy(messageImage.id).as(messageImage));

        final Map<Long, MessageText> messageTextMap = queryFactory
                .from(messageText)
                .where(messageText.id.in(messageIds))
                .transform(groupBy(messageText.id).as(messageText));

        final List<MessageDTO> messageDTOs = messages.stream()
                .map(m -> {
                    switch (m.getDtype()) {
                        case "POST":
                            return new MessageDTO(messagePostMap.get(m.getId()));
                        case "STORY":
                            return new MessageDTO(messageStoryMap.get(m.getId()));
                        case "IMAGE":
                            return new MessageDTO(messageImageMap.get(m.getId()));
                        case "TEXT":
                            return new MessageDTO(messageTextMap.get(m.getId()));
                    }
                    return null;
                })
                .collect(Collectors.toList());

        final Map<Long, List<MessageLike>> messageLikeMap = queryFactory
                .from(messageLike)
                .where(messageLike.message.id.in(messageIds))
                .innerJoin(messageLike.member, member).fetchJoin()
                .transform(groupBy(messageLike.message.id).as(list(messageLike)));

        messageDTOs.forEach(m -> {
            if (messageLikeMap.containsKey(m.getMessageId())) {
                final List<MemberDTO> likeMembers = messageLikeMap.get(m.getMessageId()).stream()
                        .map(ml -> new MemberDTO(ml.getMember()))
                        .collect(Collectors.toList());
                m.setLikeMembers(likeMembers);
            }
        });

        final long total = queryFactory
                .selectFrom(message)
                .where(
                        message.room.id.eq(findJoinRoom.getRoom().getId()).and(
                                message.createdDate.goe(findJoinRoom.getCreatedDate())
                        )
                )
                .fetchCount();

        return new PageImpl<>(messageDTOs, pageable, total);
    }
}
