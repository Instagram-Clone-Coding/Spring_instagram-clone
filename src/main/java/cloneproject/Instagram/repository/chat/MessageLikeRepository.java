package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.entity.chat.Message;
import cloneproject.Instagram.entity.chat.MessageLike;
import cloneproject.Instagram.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageLikeRepository extends JpaRepository<MessageLike, Long> {

    Optional<MessageLike> findByMemberAndMessage(Member member, Message message);
}
