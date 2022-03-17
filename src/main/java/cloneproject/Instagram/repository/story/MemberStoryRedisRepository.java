package cloneproject.Instagram.repository.story;

import cloneproject.Instagram.entity.member.MemberStory;
import org.springframework.data.repository.CrudRepository;

public interface MemberStoryRedisRepository extends CrudRepository<MemberStory, Long> {
}
