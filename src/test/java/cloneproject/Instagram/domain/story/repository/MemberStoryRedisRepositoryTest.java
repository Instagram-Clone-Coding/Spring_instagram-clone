package cloneproject.Instagram.domain.story.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;

import cloneproject.Instagram.domain.story.entity.redis.MemberStory;
import cloneproject.Instagram.global.config.EmbeddedRedisConfig;
import cloneproject.Instagram.global.config.RedisConfig;
import cloneproject.Instagram.util.domain.story.redis.MemberStoryUtils;

@DataRedisTest
@Import({EmbeddedRedisConfig.class, RedisConfig.class})
class MemberStoryRedisRepositoryTest {

	@Autowired
	private MemberStoryRedisRepository memberStoryRedisRepository;

	@Test
	void findAllByMemberId_MemberSaved3Stories_Find3Stories() throws Exception {
		// given
		final long memberId = 1L;
		final int storyCount = 3;

		for (long storyId = 1; storyId <= storyCount; storyId++) {
			memberStoryRedisRepository.save(MemberStoryUtils.of(memberId, storyId));
		}

		// when
		final List<MemberStory> findMemberStory = memberStoryRedisRepository.findAllByMemberId(memberId);

		// then
		assertThat(findMemberStory.size()).isEqualTo(storyCount);
	}

}