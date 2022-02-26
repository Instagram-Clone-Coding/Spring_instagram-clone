package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.entity.chat.MessagePost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessagePostRepository extends JpaRepository<MessagePost, Long> {
}
