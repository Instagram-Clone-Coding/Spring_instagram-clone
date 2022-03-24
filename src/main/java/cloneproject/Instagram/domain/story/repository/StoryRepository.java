package cloneproject.Instagram.domain.story.repository;

import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.story.entity.Story;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoryRepository extends JpaRepository<Story, Long> {

    Optional<Story> findByMember(Member member);
}
