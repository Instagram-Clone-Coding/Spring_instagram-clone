package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.entity.chat.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long>, MessageRepositoryQuerydsl {
}
