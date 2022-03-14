package cloneproject.Instagram.repository.story;

import cloneproject.Instagram.entity.chat.MessageStory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageStoryRepository extends JpaRepository<MessageStory, Long> {
}
