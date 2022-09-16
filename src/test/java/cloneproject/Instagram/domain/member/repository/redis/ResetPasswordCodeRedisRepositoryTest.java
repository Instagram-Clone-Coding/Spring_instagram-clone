package cloneproject.Instagram.domain.member.repository.redis;

import static org.assertj.core.api.Assertions.*;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;

import cloneproject.Instagram.domain.member.entity.redis.ResetPasswordCode;
import cloneproject.Instagram.global.config.EmbeddedRedisConfig;
import cloneproject.Instagram.global.config.RedisConfig;
import cloneproject.Instagram.util.domain.member.MemberUtils;
import cloneproject.Instagram.util.domain.member.redis.ResetPasswordCodeUtils;

@DataRedisTest
@Import({EmbeddedRedisConfig.class, RedisConfig.class})
public class ResetPasswordCodeRedisRepositoryTest {

	@Autowired
	private ResetPasswordCodeRedisRepository resetPasswordCodeRedisRepository;

	@Nested
	class FindByUsername {

		@Test
		void resetPasswordCodeExist_ReturnResetPasswordCode() {
			// given
			final ResetPasswordCode resetPasswordCode = ResetPasswordCodeUtils.newInstance();
			resetPasswordCodeRedisRepository.save(resetPasswordCode);

			// when
			final boolean isPresent = resetPasswordCodeRedisRepository.findByUsername(resetPasswordCode.getUsername())
				.isPresent();

			// then
			assertThat(isPresent).isTrue();
		}

		@Test
		void resetPasswordCodeNotExist_ReturnEmpty() {
			// given
			final String randomUsername = MemberUtils.getRandomUsername();

			// when
			final boolean isEmpty = resetPasswordCodeRedisRepository.findByUsername(randomUsername).isEmpty();

			// then
			assertThat(isEmpty).isTrue();
		}

	}

}
