package cloneproject.Instagram.util.domain.story.redis;

import org.apache.commons.lang3.RandomStringUtils;

import cloneproject.Instagram.domain.story.entity.redis.MemberStory;

public class MemberStoryUtils {

	public static MemberStory newInstance() {
		final long memberId = Long.parseLong(RandomStringUtils.random(20, false, true));
		final long storyId = Long.parseLong(RandomStringUtils.random(20, false, true));
		return of(memberId, storyId);
	}

	public static MemberStory of(Long memberId, Long storyId) {
		return MemberStory.builder()
			.memberId(memberId)
			.storyId(storyId)
			.build();
	}

}
