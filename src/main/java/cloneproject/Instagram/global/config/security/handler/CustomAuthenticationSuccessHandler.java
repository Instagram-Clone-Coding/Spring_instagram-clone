package cloneproject.Instagram.global.config.security.handler;

import static cloneproject.Instagram.global.result.ResultCode.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import cloneproject.Instagram.domain.member.dto.JwtDto;
import cloneproject.Instagram.domain.member.dto.JwtResponse;
import cloneproject.Instagram.domain.member.service.RefreshTokenService;
import cloneproject.Instagram.global.result.ResultCode;
import cloneproject.Instagram.global.result.ResultResponse;
import cloneproject.Instagram.global.util.JwtUtil;
import cloneproject.Instagram.global.util.RequestExtractor;
import cloneproject.Instagram.infra.location.LocationService;
import cloneproject.Instagram.infra.location.dto.Location;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final JwtUtil jwtUtil;
	private final LocationService locationService;
	private final RefreshTokenService refreshTokenService;
	private Map<String, ResultCode> resultCodeMap;
	private final int REFRESH_TOKEN_EXPIRES = 60 * 60 * 24 * 7; // 7일
	private final ResultCode DEFAULT_RESULT_CODE = LOGIN_SUCCESS;

	@Value("${cookie-domain}")
	private String COOKIE_DOMAIN;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authentication) throws IOException, ServletException {
		this.onAuthenticationSuccess(request, response, authentication);
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		final JwtDto jwtDto = jwtUtil.generateJwtDto(authentication);
		final Location location = locationService.getLocation(RequestExtractor.getClientIP(request));
		refreshTokenService.addRefreshToken(Long.valueOf(authentication.getName()), jwtDto.getRefreshToken(),
			RequestExtractor.getDevice(request), location);

		final JwtResponse jwtResponse = JwtResponse.builder()
			.type(jwtDto.getType())
			.accessToken(jwtDto.getAccessToken())
			.build();

		addCookie(response, jwtDto.getRefreshToken());

		final ResultCode resultCode = getResultCode(request);

		response.setStatus(resultCode.getStatus());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		try (OutputStream os = response.getOutputStream()) {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.writeValue(os, ResponseEntity.ok(ResultResponse.of(resultCode, jwtResponse)).getBody());
			os.flush();
		}
	}

	protected ResultCode getResultCode(HttpServletRequest request) {
		if (resultCodeMap != null && resultCodeMap.containsKey(request.getRequestURI())) {
			return resultCodeMap.get(request.getRequestURI());
		} else {
			return DEFAULT_RESULT_CODE;
		}
	}

	protected void addCookie(HttpServletResponse response, String refreshTokenString) {
		final Cookie cookie = new Cookie("refreshToken", refreshTokenString);

		cookie.setMaxAge(REFRESH_TOKEN_EXPIRES);

		// cookie.setSecure(true); https 미지원
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setDomain(COOKIE_DOMAIN);

		response.addCookie(cookie);
	}

	public void setResultCodeMap(Map<String, ResultCode> resultCodeMap) {
		this.resultCodeMap = resultCodeMap;
	}

}
