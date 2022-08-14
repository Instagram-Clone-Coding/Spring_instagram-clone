package cloneproject.Instagram.global.config.security.token;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class ResetPasswordCodeAuthenticationToken extends UsernamePasswordAuthenticationToken {

	private ResetPasswordCodeAuthenticationToken(Object principal, Object credentials) {
		super(principal, credentials);
	}

	public static ResetPasswordCodeAuthenticationToken of(String username, String emailCode) {
		return new ResetPasswordCodeAuthenticationToken(username, emailCode);
	}

}
