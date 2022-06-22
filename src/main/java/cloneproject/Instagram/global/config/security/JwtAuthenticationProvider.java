package cloneproject.Instagram.global.config.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import cloneproject.Instagram.domain.member.exception.JwtExpiredException;
import cloneproject.Instagram.domain.member.exception.JwtInvalidException;
import cloneproject.Instagram.global.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

	private final JwtUtil jwtUtil;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		final String jwt = (String)authentication.getPrincipal();
		try {
			return jwtUtil.getAuthentication(jwt);
		} catch (ExpiredJwtException e) {
			throw new JwtExpiredException();
		} catch (Exception e) {
			throw new JwtInvalidException();
		}
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return JwtAuthenticationToken.class.isAssignableFrom(aClass);
	}

}
