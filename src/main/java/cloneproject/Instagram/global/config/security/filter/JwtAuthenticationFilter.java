package cloneproject.Instagram.global.config.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cloneproject.Instagram.global.config.security.token.JwtAuthenticationToken;
import cloneproject.Instagram.global.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	public static final String AUTHORIZATION_HEADER = "Authorization";
	private final JwtUtil jwtUtil;

	public JwtAuthenticationFilter(RequestMatcher matcher, JwtUtil jwtUtil) {
		super(matcher);
		this.jwtUtil = jwtUtil;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {
		final String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
		final String jwt = jwtUtil.extractJwt(authorizationHeader);
		final JwtAuthenticationToken authentication = JwtAuthenticationToken.of(jwt);

		return super.getAuthenticationManager().authenticate(authentication);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authResult) throws IOException, ServletException {
		SecurityContextHolder.getContext().setAuthentication(authResult);
		chain.doFilter(request, response);
	}

}
