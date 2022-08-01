package cloneproject.Instagram.domain.story.entity.redis;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import lombok.Builder;
import lombok.Getter;

@Getter
@RedisHash(value = "memberStories", timeToLive = 86400)
public class MemberStory {

	@Id
	private String id;

	@Indexed
	private Long memberId;

	private Long storyId;

	@Builder
	public MemberStory(Long memberId, Long storyId) {
		this.memberId = memberId;
		this.storyId = storyId;
	}

}
