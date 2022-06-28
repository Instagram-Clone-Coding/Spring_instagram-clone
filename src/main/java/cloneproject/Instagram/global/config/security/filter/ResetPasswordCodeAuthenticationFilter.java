package cloneproject.Instagram.global.config.security.filter;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import cloneproject.Instagram.domain.member.dto.LoginWithCodeRequest;
import cloneproject.Instagram.global.config.security.token.ResetPasswordCodeAuthenticationToken;

public class ResetPasswordCodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
	private static final AntPathRequestMatcher ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/login/recovery",
		"POST");
	ObjectMapper objectMapper = new ObjectMapper();

	public ResetPasswordCodeAuthenticationFilter() {
		super(ANT_PATH_REQUEST_MATCHER);
	}

	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {
		if (!request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		} else {
			try {
				final String requestBody = IOUtils.toString(request.getReader());
				final LoginWithCodeRequest loginWithCodeRequest = objectMapper.readValue(requestBody,
					LoginWithCodeRequest.class);

				final ResetPasswordCodeAuthenticationToken authRequest = ResetPasswordCodeAuthenticationToken.of(
					loginWithCodeRequest.getUsername(), loginWithCodeRequest.getCode());
				return this.getAuthenticationManager().authenticate(authRequest);
			} catch (IOException e) {
				throw new AuthenticationServiceException("Authentication failed while converting request body.");
			}
		}
	}

}
