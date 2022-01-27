package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.entity.chat.MessageLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageLikeRepository extends JpaRepository<MessageLike, Long> {
}
