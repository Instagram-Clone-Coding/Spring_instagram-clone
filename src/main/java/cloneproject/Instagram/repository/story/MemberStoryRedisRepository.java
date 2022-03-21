package cloneproject.Instagram.repository.story;

import cloneproject.Instagram.entity.member.MemberStory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MemberStoryRedisRepository extends CrudRepository<MemberStory, Long> {

    List<MemberStory> findAllByMemberId(Long memberId);
}
