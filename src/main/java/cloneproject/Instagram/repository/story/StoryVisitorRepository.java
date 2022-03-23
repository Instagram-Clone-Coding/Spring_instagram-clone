package cloneproject.Instagram.repository.story;

import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.entity.story.Story;
import cloneproject.Instagram.entity.story.StoryVisitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoryVisitorRepository extends JpaRepository<StoryVisitor, Long> {

    List<StoryVisitor> findAllByStoryInAndMember(List<Story> stories, Member member);
}
