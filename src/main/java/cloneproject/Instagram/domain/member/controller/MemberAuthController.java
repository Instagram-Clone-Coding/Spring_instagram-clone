package cloneproject.Instagram.domain.member.controller;

import static cloneproject.Instagram.global.result.ResultCode.*;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cloneproject.Instagram.domain.member.dto.JwtDto;
import cloneproject.Instagram.domain.member.dto.JwtResponse;
import cloneproject.Instagram.domain.member.dto.LoginRequest;
import cloneproject.Instagram.domain.member.dto.LoginWithCodeRequest;
import cloneproject.Instagram.domain.member.dto.LoginDevicesDTO;
import cloneproject.Instagram.domain.member.dto.RegisterRequest;
import cloneproject.Instagram.domain.member.dto.ResetPasswordRequest;
import cloneproject.Instagram.domain.member.dto.SendConfirmationEmailRequest;
import cloneproject.Instagram.domain.member.dto.UpdatePasswordRequest;
import cloneproject.Instagram.domain.member.exception.JwtInvalidException;
import cloneproject.Instagram.domain.member.service.MemberAuthService;
import cloneproject.Instagram.global.result.ResultResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(tags = "멤버 인증 API")
@Validated
@RestController
@RequiredArgsConstructor
public class MemberAuthController {

	private final MemberAuthService memberAuthService;
	private final int REFRESH_TOKEN_EXPIRES = 60 * 60 * 24 * 7; // 7일

	@Value("${cookie-domain}")
	private String COOKIE_DOMAIN;

	@ApiOperation(value = "username 중복 조회")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " "),
		@ApiImplicitParam(name = "username", value = "유저네임", required = true, example = "dlwlrma")
	})
	@GetMapping(value = "/accounts/check")
	public ResponseEntity<ResultResponse> checkUsername(
		@RequestParam
		@Length(min = 4, max = 12, message = "사용자 이름은 4문자 이상 12문자 이하여야 합니다")
		@Pattern(regexp = "^[0-9a-zA-Z]+$", message = "username엔 대소문자, 숫자만 사용할 수 있습니다.")
		String username) {
		final boolean check = memberAuthService.checkUsername(username);
		if (check) {
			return ResponseEntity.ok(ResultResponse.of(CHECK_USERNAME_GOOD, true));
		} else {
			return ResponseEntity.ok(ResultResponse.of(CHECK_USERNAME_BAD, false));
		}
	}

	@ApiOperation(value = "회원가입")
	@ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " ")
	@PostMapping(value = "/accounts")
	public ResponseEntity<ResultResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
		final boolean isRegistered = memberAuthService.register(registerRequest);
		if (isRegistered) {
			return ResponseEntity.ok(ResultResponse.of(REGISTER_SUCCESS, true));
		} else {
			return ResponseEntity.ok(ResultResponse.of(CONFIRM_EMAIL_FAIL, false));
		}
	}

	@ApiOperation(value = "인증코드 이메일 전송")
	@ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " ")
	@PostMapping(value = "/accounts/email")
	public ResponseEntity<ResultResponse> sendConfirmEmail(
		@Valid @RequestBody SendConfirmationEmailRequest sendConfirmationEmailRequest) {
		memberAuthService.sendEmailConfirmation(sendConfirmationEmailRequest.getUsername(),
			sendConfirmationEmailRequest.getEmail());

		return ResponseEntity.ok(ResultResponse.of(SEND_CONFIRM_EMAIL_SUCCESS));
	}

	@ApiOperation(value = "로그인")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " ")
	})
	@PostMapping(value = "/login")
	public ResponseEntity<ResultResponse> login(@Valid @RequestBody LoginRequest loginRequest,
		HttpServletRequest request, HttpServletResponse response) {

		return ResponseEntity.ok(ResultResponse.of(LOGIN_SUCCESS, null));
	}

	@ApiOperation(value = "토큰 재발급")
	@ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " ")
	@PostMapping(value = "/reissue")
	public ResponseEntity<ResultResponse> reissue(
		@CookieValue(value = "refreshToken", required = false) Cookie refreshCookie, HttpServletResponse response) {
			return ResponseEntity.ok(ResultResponse.of(REISSUE_SUCCESS, null));
	}

	@ApiOperation(value = "비밀번호 변경")
	@ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " ")
	@PutMapping(value = "/accounts/password")
	public ResponseEntity<ResultResponse> updatePassword(
		@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest) {
		memberAuthService.updatePassword(updatePasswordRequest);

		return ResponseEntity.ok(ResultResponse.of(UPDATE_PASSWORD_SUCCESS));
	}

	@ApiOperation(value = "비밀번호변경 이메일 전송")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " "),
		@ApiImplicitParam(name = "username", value = "유저네임", required = true, example = "dlwlrma")
	})
	@PostMapping(value = "/accounts/password/email")
	public ResponseEntity<ResultResponse> sendResetPasswordCode(
		@RequestParam @Length(min = 4, max = 12, message = "사용자 이름은 4문자 이상 12문자 이하여야 합니다")
		@Pattern(regexp = "^[0-9a-zA-Z]+$", message = "username엔 대소문자, 숫자만 사용할 수 있습니다.") String username) {
		final String email = memberAuthService.sendResetPasswordCode(username);

		return ResponseEntity.ok(ResultResponse.of(SEND_RESET_PASSWORD_EMAIL_SUCCESS, email));
	}

	// TODO filter로 옮길 방법?
	@ApiOperation(value = "코드를 통한 비밀번호 재설정")
	@ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " ")
	@PutMapping(value = "/accounts/password/reset")
	public ResponseEntity<ResultResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest,
		HttpServletRequest request,
		HttpServletResponse response) {
		final String device = request.getHeader("user-agent");
		final String ip = getClientIP(request);
		final JwtDto jwt = memberAuthService.resetPassword(resetPasswordRequest, device, ip);

		final Cookie cookie = getRefreshTokenCookie(jwt.getRefreshToken());

		response.addCookie(cookie);

		final JwtResponse jwtResponse = JwtResponse.builder()
			.type(jwt.getType())
			.accessToken(jwt.getAccessToken())
			.build();

		return ResponseEntity.ok(ResultResponse.of(RESET_PASSWORD_SUCCESS, jwtResponse));
	}

	@ApiOperation(value = "코드를 통한 로그인")
	@ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " ")
	@PostMapping(value = "/login/recovery")
	public ResponseEntity<ResultResponse> loginWithCode(
		@Valid @RequestBody LoginWithCodeRequest loginWithCodeRequest,
		HttpServletRequest request,
		HttpServletResponse response) {
		return ResponseEntity.ok(ResultResponse.of(LOGIN_WITH_CODE_SUCCESS, null));
	}

	@ApiOperation(value = "로그아웃")
	@PostMapping(value = "/logout")
	public ResponseEntity<ResultResponse> logout(
		@CookieValue(value = "refreshToken", required = false) Cookie refreshCookie,
		HttpServletResponse response) {
		if (refreshCookie != null) {
			memberAuthService.logout(refreshCookie.getValue());
		}

		final Cookie cookie = new Cookie("refreshToken", null);

		cookie.setMaxAge(0);

		// cookie.setSecure(true); https 미지원
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setDomain(COOKIE_DOMAIN);

		response.addCookie(cookie);

		return ResponseEntity.ok(ResultResponse.of(LOGOUT_SUCCESS));
	}

	@ApiOperation(value = "비밀번호 재설정 코드 검증")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " "),
		@ApiImplicitParam(name = "username", value = "유저네임", required = true, example = "dlwlrma"),
		@ApiImplicitParam(name = "code", value = "인증코드", required = true, example = "AAABBB")
	})
	@GetMapping(value = "/accounts/password/reset")
	public ResponseEntity<ResultResponse> checkResetPasswordCode(
		@RequestParam @Length(min = 4, max = 12, message = "사용자 이름은 4문자 이상 12문자 이하여야 합니다")
		@Pattern(regexp = "^[0-9a-zA-Z]+$", message = "username엔 대소문자, 숫자만 사용할 수 있습니다.") String username,
		@RequestParam @Length(min = 30, max = 30, message = "인증코드는 30글자 입니다") String code) {
		final boolean check = memberAuthService.checkResetPasswordCode(username, code);
		if (check) {
			return ResponseEntity.ok(ResultResponse.of(CHECK_RESET_PASSWORD_CODE_GOOD, true));
		} else {
			return ResponseEntity.ok(ResultResponse.of(CHECK_RESET_PASSWORD_CODE_BAD, false));
		}
	}

	@ApiOperation(value = "로그인한 기기 조회")
	@GetMapping(value = "/accounts/login/device")
	public ResponseEntity<ResultResponse> getLoginDevices() {
		final List<LoginDevicesDTO> loginDevicesDTOs = memberAuthService.getLoginDevices();

		return ResponseEntity.ok(ResultResponse.of(GET_LOGIN_DEVICES_SUCCESS, loginDevicesDTOs));
	}

	@ApiOperation(value = "기기 로그아웃 시키기")
	@PostMapping(value = "/logout/device")
	public ResponseEntity<ResultResponse> logoutDevice(@RequestParam String tokenId) {
		memberAuthService.logoutDevice(tokenId);

		return ResponseEntity.ok(ResultResponse.of(LOGOUT_DEVICE_SUCCESS));
	}

	private Cookie getRefreshTokenCookie(String refreshTokenString) {
		Cookie cookie = new Cookie("refreshToken", refreshTokenString);

		cookie.setMaxAge(REFRESH_TOKEN_EXPIRES);

		// cookie.setSecure(true); https 미지원
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setDomain(COOKIE_DOMAIN);

		return cookie;
	}

	private String getClientIP(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null)
			ip = request.getHeader("Proxy-Client-IP");
		if (ip == null)
			ip = request.getHeader("WL-Proxy-Client-IP");
		if (ip == null)
			ip = request.getHeader("HTTP_CLIENT_IP");
		if (ip == null)
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		if (ip == null)
			ip = request.getRemoteAddr();
		return ip;
	}

}
