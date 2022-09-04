package cloneproject.Instagram.domain.member.repository.redis;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;

import cloneproject.Instagram.domain.member.entity.redis.RefreshToken;
import cloneproject.Instagram.global.config.EmbeddedRedisConfig;
import cloneproject.Instagram.global.config.RedisConfig;
import cloneproject.Instagram.util.domain.member.redis.RefreshTokenUtils;

@DataRedisTest
@Import({EmbeddedRedisConfig.class, RedisConfig.class})
public class RefreshTokenRedisRepositoryTest {

	@Autowired
	private RefreshTokenRedisRepository refreshTokenRedisRepository;

	@Nested
	class FindAllByMemberId {

		@Test
		void memberHas3RefreshTokens_Find3RefreshTokens() {
			// given
			final Long memberId = 1L;
			final int refreshTokenCount = 3;

			for (int count = 1; count <= refreshTokenCount; count++) {
				refreshTokenRedisRepository.save(RefreshTokenUtils.newInstance(memberId));
			}

			// when
			final List<RefreshToken> refreshTokens = refreshTokenRedisRepository.findAllByMemberId(memberId);

			// then
			assertThat(refreshTokens.size()).isEqualTo(refreshTokenCount);
		}

	}

	@Nested
	class FindByMemberIdAndValue {

		@Test
		void refreshTokenExist_ReturnRefreshToken() {
			// given
			final Long memberId = 1L;

			final RefreshToken refreshToken = RefreshTokenUtils.newInstance(memberId);
			refreshTokenRedisRepository.save(refreshToken);

			// when
			final boolean isPresent = refreshTokenRedisRepository.findByMemberIdAndValue(memberId,
				refreshToken.getValue()).isPresent();

			// then
			assertThat(isPresent).isTrue();
		}

		@Test
		void refreshTokenNotExist_ReturnEmpty() {
			// given
			final Long memberId = 1L;
			final String randomValue = RandomStringUtils.random(15, true, true);

			// when
			final boolean isEmpty = refreshTokenRedisRepository.findByMemberIdAndValue(memberId,
				randomValue).isEmpty();

			// then
			assertThat(isEmpty).isTrue();
		}

	}

	@Nested
	class FindByMemberIdAndId {

		@Test
		void refreshTokenExist_ReturnRefreshToken() {
			// given
			final Long memberId = 1L;

			final RefreshToken refreshToken = RefreshTokenUtils.newInstance(memberId);
			refreshTokenRedisRepository.save(refreshToken);

			// when
			final boolean isPresent = refreshTokenRedisRepository.findByMemberIdAndId(memberId,
				refreshToken.getId()).isPresent();

			// then
			assertThat(isPresent).isTrue();
		}

		@Test
		void refreshTokenNotExist_ReturnEmpty() {
			// given
			final Long memberId = 1L;
			final String randomId = RandomStringUtils.random(2, false, true);

			// when
			final boolean isEmpty = refreshTokenRedisRepository.findByMemberIdAndId(memberId,
				randomId).isEmpty();

			// then
			assertThat(isEmpty).isTrue();
		}

	}

}
