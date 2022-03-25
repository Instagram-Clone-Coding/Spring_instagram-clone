package cloneproject.Instagram.domain.story.repository;

import org.springframework.data.repository.CrudRepository;

import cloneproject.Instagram.domain.story.entity.redis.MemberStory;

import java.util.List;

public interface MemberStoryRedisRepository extends CrudRepository<MemberStory, Long> {

    List<MemberStory> findAllByMemberId(Long memberId);
}
