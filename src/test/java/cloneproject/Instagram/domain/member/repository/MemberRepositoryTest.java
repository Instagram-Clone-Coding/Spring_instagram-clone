package cloneproject.Instagram.domain.member.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
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

	@Test
	void findByUsername_MemberExist_ReturnMember() {
		// given
		final Member givenMember = MemberUtils.newInstance();
		memberRepository.save(givenMember);

		// when
		final Optional<Member> member = memberRepository.findByUsername(givenMember.getUsername());

		// then
		assertThat(member.isPresent()).isTrue();
	}

	@Test
	void findByUsername_MemberNotExist_ReturnEmpty() {
		// given
		final String randomUsername = RandomStringUtils.random(20, true, true);

		// when
		final Optional<Member> member = memberRepository.findByUsername(randomUsername);

		// then
		assertThat(member.isEmpty()).isTrue();
	}

	@Test
	void existsByUsername_MemberExist_ReturnTrue() {
		// given
		final Member givenMember = MemberUtils.newInstance();
		memberRepository.save(givenMember);

		// when
		final boolean memberExists = memberRepository.existsByUsername(givenMember.getUsername());

		// then
		assertThat(memberExists).isTrue();
	}

	@Test
	void existsByUsername_MemberNotExist_ReturnFalse() {
		// given
		final String randomUsername = RandomStringUtils.random(20, true, true);

		// when
		final boolean memberExists = memberRepository.existsByUsername(randomUsername);

		// then
		assertThat(memberExists).isFalse();
	}

	@Test
	void findAllByUsernameIn_3MembersExist_Find3Members() {
		// given
		final Set<String> usernames = new HashSet<>();
		while (usernames.size() < 3) {
			final Member givenMember = MemberUtils.newInstance();
			if (usernames.contains(givenMember.getUsername())) {
				continue;
			}
			memberRepository.save(givenMember);
			usernames.add(givenMember.getUsername());
		}

		// when
		final List<Member> members = memberRepository.findAllByUsernameIn(usernames);

		// then
		assertThat(members.size()).isEqualTo(usernames.size());
	}

	@Test
	void findAllByIdIn_3MembersExist_Find3Members() {
		// given
		final Set<String> usernames = new HashSet<>();
		final Set<Long> ids = new HashSet<>();
		while (usernames.size() < 3) {
			final Member givenMember = MemberUtils.newInstance();
			if (usernames.contains(givenMember.getUsername())) {
				continue;
			}
			memberRepository.save(givenMember);
			usernames.add(givenMember.getUsername());
			ids.add(givenMember.getId());
		}

		// when
		final List<Member> members = memberRepository.findAllByIdIn(ids);

		// then
		assertThat(members.size()).isEqualTo(usernames.size());
	}

}
