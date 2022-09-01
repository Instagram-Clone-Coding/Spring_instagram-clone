package cloneproject.Instagram.domain.member.repository.querydsl;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import cloneproject.Instagram.domain.member.entity.Block;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.repository.BlockRepository;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.global.config.QuerydslConfig;
import cloneproject.Instagram.util.domain.member.MemberUtils;

@DataJpaTest
@Import(QuerydslConfig.class)
public class BlockRepositoryQuerydslTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private BlockRepository blockRepository;

	@Nested
	class IsBlockingOrIsBlocked {

		@Test
		void blockingTarget_ReturnTrue() {
			// given
			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final Member target = MemberUtils.newInstance();
			memberRepository.save(target);

			final Block block = Block.builder()
				.member(member)
				.blockMember(target)
				.build();
			blockRepository.save(block);

			// when
			final boolean isBlockingOrIsBlocked = blockRepository.isBlockingOrIsBlocked(member.getId(), target.getId());

			//then
			assertThat(isBlockingOrIsBlocked).isTrue();
		}

		@Test
		void blockedByTarget_ReturnTrue() {
			// given
			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final Member target = MemberUtils.newInstance();
			memberRepository.save(target);

			final Block block = Block.builder()
				.member(target)
				.blockMember(member)
				.build();
			blockRepository.save(block);

			// when
			final boolean isBlockingOrIsBlocked = blockRepository.isBlockingOrIsBlocked(member.getId(), target.getId());

			//then
			assertThat(isBlockingOrIsBlocked).isTrue();
		}

		@Test
		void notBlockingEachOther_ReturnFalse() {
			// given
			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final Member target = MemberUtils.newInstance();
			memberRepository.save(target);

			// when
			final boolean isBlockingOrIsBlocked = blockRepository.isBlockingOrIsBlocked(member.getId(), target.getId());

			//then
			assertThat(isBlockingOrIsBlocked).isFalse();
		}

	}

}
