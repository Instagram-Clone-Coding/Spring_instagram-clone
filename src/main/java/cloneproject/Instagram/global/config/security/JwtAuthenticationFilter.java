package cloneproject.Instagram.global.config.security;

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

import cloneproject.Instagram.global.error.exception.JwtAuthenticationException;
import cloneproject.Instagram.global.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private final RequestMatcher matcher;
	private final JwtUtil jwtUtil;
	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String BEARER_PREFIX = "Bearer ";

	public JwtAuthenticationFilter(RequestMatcher matcher, JwtUtil jwtUtil){
		super("/**");
		this.matcher = matcher;
		this.jwtUtil = jwtUtil;
	}


	@Override
	protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
		return matcher.matches(request);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {
		final String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
		final String jwt;
		try {
			jwt = jwtUtil.extractJwt(authorizationHeader);
			final JwtAuthenticationToken authentication = JwtAuthenticationToken.of(jwt);

			return super.getAuthenticationManager().authenticate(authentication);
		} catch (Exception e) {
			throw new JwtAuthenticationException();
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authResult) throws IOException, ServletException {
		SecurityContextHolder.getContext().setAuthentication(authResult);
		chain.doFilter(request, response);
		// super.successfulAuthentication(request, response, chain, authResult);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException, ServletException {
		SecurityContextHolder.clearContext();
		// super.unsuccessfulAuthentication(request, response, failed);
	}

}
