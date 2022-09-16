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
			final String randomUsername = MemberUtils.getRandomUsername();

			// when
			final boolean isEmpty = memberRepository.findByUsername(randomUsername).isEmpty();

			// then
			assertThat(isEmpty).isTrue();
		}

	}

	@Nested
	class ExistsByUsername {

		@Test
		void memberExist_ReturnTrue() {
			// given
			final Member givenMember = MemberUtils.newInstance();
			memberRepository.save(givenMember);

			// when
			final boolean isPresent = memberRepository.existsByUsername(givenMember.getUsername());

			// then
			assertThat(isPresent).isTrue();
		}

		@Test
		void memberNotExist_ReturnFalse() {
			// given
			final String randomUsername = MemberUtils.getRandomUsername();

			// when
			final boolean isPresent = memberRepository.existsByUsername(randomUsername);

			// then
			assertThat(isPresent).isFalse();
		}

	}

	@Nested
	class FindAllByUsernameIn {

		@Test
		void existent3Usernames_Find3Members() {
			// given
			final long memberCount = 3;
			final List<Member> givenMembers = MemberUtils.newDistinctInstances(memberCount);
			memberRepository.saveAll(givenMembers);
			final List<String> existent3Usernames = MemberUtils.getUsernamesFromMemberList(givenMembers);

			// when
			final List<Member> members = memberRepository.findAllByUsernameIn(existent3Usernames);

			// then
			assertThat(members.size()).isEqualTo(memberCount);
		}

	}

	@Nested
	class FindAllByIdIn {

		@Test
		void existent3Ids_Find3Members() {
			// given
			final long memberCount = 3;
			final List<Member> givenMembers = MemberUtils.newDistinctInstances(memberCount);
			memberRepository.saveAll(givenMembers);
			final List<Long> existent3Ids = MemberUtils.getIdsFromMemberList(givenMembers);

			// when
			final List<Member> members = memberRepository.findAllByIdIn(existent3Ids);

			// then
			assertThat(members.size()).isEqualTo(memberCount);
		}

	}

}
