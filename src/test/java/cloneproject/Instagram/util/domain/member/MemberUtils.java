package cloneproject.Instagram.util.domain.member;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;

import cloneproject.Instagram.domain.member.entity.Member;

public class MemberUtils {

	public static String getRandomUsername() {
		return RandomStringUtils.random(15, true, true);
	}

	public static Member newInstance() {
		final String username = RandomStringUtils.random(20, true, true);
		final String password = RandomStringUtils.random(20, true, true);
		final String email = RandomStringUtils.random(20, true, true) + "gmail.com";
		final String name = RandomStringUtils.random(20, true, true);
		return of(username, password, email, name);
	}

	public static List<Member> newDistinctInstances(long memberCount) {
		final List<Member> members = new ArrayList<>();
		final Set<String> usernames = new HashSet<>();
		while (usernames.size() < memberCount) {
			final Member member = newInstance();
			if (usernames.contains(member.getUsername())) {
				continue;
			}
			members.add(member);
			usernames.add(member.getUsername());
		}
		return members;
	}

	public static Member of(String username, String password, String email, String name) {
		return Member.builder()
			.username(username)
			.password(password)
			.email(email)
			.name(name)
			.build();
	}

	public static List<String> getUsernamesFromMemberList(List<Member> members) {
		return members.stream()
			.map(Member::getUsername)
			.collect(Collectors.toList());
	}

	public static List<Long> getIdsFromMemberList(List<Member> members) {
		return members.stream()
			.map(Member::getId)
			.collect(Collectors.toList());
	}

}
