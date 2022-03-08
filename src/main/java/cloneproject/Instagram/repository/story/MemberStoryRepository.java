package cloneproject.Instagram.repository.story;

import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.entity.member.MemberStory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberStoryRepository extends JpaRepository<MemberStory, Long> {

    Optional<MemberStory> findByMember(Member member);
}
