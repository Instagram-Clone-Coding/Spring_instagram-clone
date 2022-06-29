package cloneproject.Instagram.global.config.security.provider;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import cloneproject.Instagram.global.config.security.token.JwtAuthenticationToken;
import cloneproject.Instagram.global.util.JwtUtil;
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
		return jwtUtil.getAuthentication(jwt);
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return JwtAuthenticationToken.class.isAssignableFrom(aClass);
	}

}
