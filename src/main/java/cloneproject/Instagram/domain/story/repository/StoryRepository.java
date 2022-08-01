package cloneproject.Instagram.domain.story.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.domain.story.entity.Story;

public interface StoryRepository extends JpaRepository<Story, Long> {

}
