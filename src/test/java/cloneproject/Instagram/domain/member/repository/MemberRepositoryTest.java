package cloneproject.Instagram.domain.member.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.global.config.QuerydslConfig;
import cloneproject.Instagram.util.domain.member.MemberUtils;

@DataJpaTest
@Import(QuerydslConfig.class)
public class MemberRepositoryTest {

	@Autowired
	private MemberRepository memberRepository;

	@Nested
	class FindByUsername {

		@Test
		void memberExist_ReturnMember() {
			// given
			final Member givenMember = MemberUtils.newInstance();
			memberRepository.save(givenMember);

			// when
			final boolean isPresent = memberRepository.findByUsername(givenMember.getUsername()).isPresent();

			// then
			assertThat(isPresent).isTrue();
		}

		@Test
		void memberNotExist_ReturnEmpty() {
			// given
			final String randomUsername = RandomStringUtils.random(15, true, true);

			// when
			final boolean isEmpty = memberRepository.findByUsername(randomUsername).isEmpty();

			// then
			assertThat(isEmpty).isTrue();
		}

	}

	@Nested
	class ExistsByUsername {

		@Test
		void existsByUsername_MemberExist_ReturnTrue() {
			// given
			final Member givenMember = MemberUtils.newInstance();
			memberRepository.save(givenMember);

			// when
			final boolean isPresent = memberRepository.existsByUsername(givenMember.getUsername());

			// then
			assertThat(isPresent).isTrue();
		}

		@Test
		void existsByUsername_MemberNotExist_ReturnFalse() {
			// given
			final String randomUsername = RandomStringUtils.random(20, true, true);

			// when
			final boolean isPresent = memberRepository.existsByUsername(randomUsername);

			// then
			assertThat(isPresent).isFalse();
		}

	}

	@Nested
	class FindAllByUsernameIn {

		@Test
		void given3Usernames_Find3Members() {
			// given
			final long memberCount = 3;
			final List<Member> givenMembers = MemberUtils.newDistinctInstances(memberCount);
			memberRepository.saveAll(givenMembers);
			final List<String> usernames = MemberUtils.getUsernamesFromMemberList(givenMembers);

			// when
			final List<Member> members = memberRepository.findAllByUsernameIn(usernames);

			// then
			assertThat(members.size()).isEqualTo(memberCount);
		}

	}

	@Nested
	class FindAllByIdIn {

		@Test
		void given3Ids_Find3Members() {
			// given
			final long memberCount = 3;
			final List<Member> givenMembers = MemberUtils.newDistinctInstances(memberCount);
			memberRepository.saveAll(givenMembers);
			final List<Long> ids = MemberUtils.getIdsFromMemberList(givenMembers);

			// when
			final List<Member> members = memberRepository.findAllByIdIn(ids);

			// then
			assertThat(members.size()).isEqualTo(memberCount);
		}

	}

}
