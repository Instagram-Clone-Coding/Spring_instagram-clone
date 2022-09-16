package cloneproject.Instagram.domain.member.repository.redis;

import static org.assertj.core.api.Assertions.*;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;

import cloneproject.Instagram.domain.member.entity.redis.RegisterCode;
import cloneproject.Instagram.global.config.EmbeddedRedisConfig;
import cloneproject.Instagram.global.config.RedisConfig;
import cloneproject.Instagram.util.domain.member.MemberUtils;
import cloneproject.Instagram.util.domain.member.redis.RegisterCodeUtils;

@DataRedisTest
@Import({EmbeddedRedisConfig.class, RedisConfig.class})
public class RegisterCodeRedisRepositoryTest {

	@Autowired
	private RegisterCodeRedisRepository registerCodeRedisRepository;

	@Nested
	class FindByUsername {

		@Test
		void registerCodeExist_ReturnRegisterCode() {
			// given
			final RegisterCode givenRegisterCode = RegisterCodeUtils.newInstance();
			registerCodeRedisRepository.save(givenRegisterCode);

			// when
			final boolean isPresent = registerCodeRedisRepository.findByUsername(givenRegisterCode.getUsername())
				.isPresent();

			// then
			assertThat(isPresent).isTrue();
		}

		@Test
		void registerCodeNotExist_ReturnEmpty() {
			// given
			final String randomUsername = MemberUtils.getRandomUsername();

			// when
			final boolean isEmpty = registerCodeRedisRepository.findByUsername(randomUsername).isEmpty();

			// then
			assertThat(isEmpty).isTrue();
		}

	}

}
