package cloneproject.Instagram.domain.story.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import cloneproject.Instagram.domain.story.entity.redis.MemberStory;

public interface MemberStoryRedisRepository extends CrudRepository<MemberStory, Long> {

	List<MemberStory> findAllByMemberId(Long memberId);

	List<MemberStory> findAllByMemberIdIn(List<Long> memberIds);

}
