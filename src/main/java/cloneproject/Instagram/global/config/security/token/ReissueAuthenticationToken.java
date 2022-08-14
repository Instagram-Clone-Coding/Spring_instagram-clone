package cloneproject.Instagram.global.config.security.token;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class ReissueAuthenticationToken extends UsernamePasswordAuthenticationToken {

	private ReissueAuthenticationToken(Object principal, Object credentials) {
		super(principal, credentials);
	}

	public static ReissueAuthenticationToken of(String refreshToken) {
		return new ReissueAuthenticationToken(refreshToken, refreshToken);
	}

}
