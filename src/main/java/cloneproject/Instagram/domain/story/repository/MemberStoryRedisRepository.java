package cloneproject.Instagram.domain.story.repository;

import org.springframework.data.repository.CrudRepository;

import cloneproject.Instagram.domain.member.entity.MemberStory;

import java.util.List;

public interface MemberStoryRedisRepository extends CrudRepository<MemberStory, Long> {

    List<MemberStory> findAllByMemberId(Long memberId);
}
