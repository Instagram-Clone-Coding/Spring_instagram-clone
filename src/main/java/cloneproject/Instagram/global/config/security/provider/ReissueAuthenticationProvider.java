package cloneproject.Instagram.global.config.security.provider;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import cloneproject.Instagram.domain.member.entity.redis.RefreshToken;
import cloneproject.Instagram.domain.member.exception.LogoutByAnotherException;
import cloneproject.Instagram.domain.member.service.RefreshTokenService;
import cloneproject.Instagram.global.config.security.token.ReissueAuthenticationToken;
import cloneproject.Instagram.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReissueAuthenticationProvider implements AuthenticationProvider {

	private final JwtUtil jwtUtil;
	private final RefreshTokenService refreshTokenService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		final String refreshTokenString = (String)authentication.getPrincipal();

		final Authentication authenticated = jwtUtil.getAuthentication(refreshTokenString);

		final String memberId = (String)authenticated.getName();

		// 사용할 수 있는(저장된) 토큰인지 확인
		final RefreshToken refreshToken = refreshTokenService.findRefreshToken(Long.valueOf(memberId),
			refreshTokenString).orElseThrow(LogoutByAnotherException::new);

		this.deleteRefreshToken(refreshToken);

		return authenticated;
	}

	protected void deleteRefreshToken(RefreshToken refreshToken) {
		refreshTokenService.deleteRefreshToken(refreshToken);
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return ReissueAuthenticationToken.class.isAssignableFrom(aClass);
	}

}
