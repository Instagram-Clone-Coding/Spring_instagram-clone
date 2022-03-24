package cloneproject.Instagram.domain.dm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.domain.dm.entity.MessageStory;

public interface MessageStoryRepository extends JpaRepository<MessageStory, Long> {
}
