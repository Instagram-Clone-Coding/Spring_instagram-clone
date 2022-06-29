package cloneproject.Instagram.global.config.security.filter;

import java.util.Arrays;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import cloneproject.Instagram.domain.member.exception.JwtInvalidException;
import cloneproject.Instagram.global.config.security.token.ReissueAuthenticationToken;

public class ReissueAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private static final AntPathRequestMatcher ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/reissue",
		"POST");

	public ReissueAuthenticationFilter() {
		super(ANT_PATH_REQUEST_MATCHER);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {
		if (!request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		} else {
			final Cookie refreshToken = Arrays.stream(request.getCookies())
				.filter(c -> c.getName().equals("refreshToken"))
				.findFirst()
				.orElseThrow(JwtInvalidException::new);
			final ReissueAuthenticationToken authRequest = ReissueAuthenticationToken.of(refreshToken.getValue());
			return this.getAuthenticationManager().authenticate(authRequest);
		}
	}

}