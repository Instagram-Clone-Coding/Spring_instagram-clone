package cloneproject.Instagram.domain.story.repository;

import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.story.entity.Story;
import cloneproject.Instagram.domain.story.entity.StoryVisitor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoryVisitorRepository extends JpaRepository<StoryVisitor, Long> {

    List<StoryVisitor> findAllByStoryInAndMember(List<Story> stories, Member member);
}
