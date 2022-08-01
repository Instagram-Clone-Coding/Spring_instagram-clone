package cloneproject.Instagram.domain.story.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.story.entity.Story;
import cloneproject.Instagram.domain.story.entity.StoryVisitor;

public interface StoryVisitorRepository extends JpaRepository<StoryVisitor, Long> {

	List<StoryVisitor> findAllByStoryInAndMember(List<Story> stories, Member member);

}
