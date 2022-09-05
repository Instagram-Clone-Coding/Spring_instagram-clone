package cloneproject.Instagram.util.domain.member.redis;

import org.apache.commons.lang3.RandomStringUtils;

import cloneproject.Instagram.domain.member.entity.redis.ResetPasswordCode;

public class ResetPasswordCodeUtils {

	public static ResetPasswordCode newInstance() {
		final String username = RandomStringUtils.random(20, true, true);
		final String code = RandomStringUtils.random(30, true, true).toUpperCase();
		return of(username, code);
	}

	public static ResetPasswordCode of(String username, String code) {
		return ResetPasswordCode.builder()
			.username(username)
			.code(code)
			.build();
	}

}
