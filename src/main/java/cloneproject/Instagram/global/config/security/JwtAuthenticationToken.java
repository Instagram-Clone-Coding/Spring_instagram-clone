package cloneproject.Instagram.global.config.security;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {

	private JwtAuthenticationToken(Object principal, Object credentials,
		Collection<? extends GrantedAuthority> authorities) {
		super(principal, credentials, authorities);
	}

	private JwtAuthenticationToken(Object principal, Object credentials) {
		// authority는 자동으로 null로 설정됨
		super(principal, credentials);
	}

	public static JwtAuthenticationToken of(String jwt) {
		return new JwtAuthenticationToken(jwt, jwt);
	}

	public static JwtAuthenticationToken of(Object principal, Object credentials,
		Collection<? extends GrantedAuthority> authorities) {
		return new JwtAuthenticationToken(principal, credentials, authorities);
	}

}
