package cloneproject.Instagram.util.domain.member;

import org.apache.commons.lang3.RandomStringUtils;

import cloneproject.Instagram.domain.member.entity.Member;

public class MemberUtils {

	public static Member newInstance() {
		final String username = RandomStringUtils.random(20, true, true);
		final String password = RandomStringUtils.random(20, true, true);
		final String email = RandomStringUtils.random(20, true, true) + "gmail.com";
		final String name = RandomStringUtils.random(20, true, true);
		return of(username, password, email, name);
	}

	public static Member of(String username, String password, String email, String name) {
		return Member.builder()
			.username(username)
			.password(password)
			.email(email)
			.name(name)
			.build();
	}

}
