package cloneproject.Instagram.util.domain.member.redis;

import org.apache.commons.lang3.RandomStringUtils;

import cloneproject.Instagram.domain.member.entity.redis.RegisterCode;

public class RegisterCodeUtils {

	public static RegisterCode newInstance() {
		final String username = RandomStringUtils.random(20, true, true);
		final String email = RandomStringUtils.random(20, true, true) + "gmail.com";
		final String code = RandomStringUtils.random(6, true, true).toUpperCase();
		return of(username, email, code);
	}

	public static RegisterCode of(String username, String email, String code) {
		return RegisterCode.builder()
			.username(username)
			.email(email)
			.code(code)
			.build();
	}

}
