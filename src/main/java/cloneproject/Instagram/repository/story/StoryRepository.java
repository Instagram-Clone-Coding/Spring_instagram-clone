package cloneproject.Instagram.repository.story;

import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.entity.story.Story;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoryRepository extends JpaRepository<Story, Long> {

    Optional<Story> findByMember(Member member);
}
