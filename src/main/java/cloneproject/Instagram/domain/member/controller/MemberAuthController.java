package cloneproject.Instagram.domain.member.controller;

import static cloneproject.Instagram.global.result.ResultCode.*;

import java.util.List;

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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import cloneproject.Instagram.domain.member.dto.JwtDto;
import cloneproject.Instagram.domain.member.dto.JwtResponse;
import cloneproject.Instagram.domain.member.dto.LoginDeviceDto;
import cloneproject.Instagram.domain.member.dto.LoginRequest;
import cloneproject.Instagram.domain.member.dto.LoginWithCodeRequest;
import cloneproject.Instagram.domain.member.dto.RegisterRequest;
import cloneproject.Instagram.domain.member.dto.ResetPasswordRequest;
import cloneproject.Instagram.domain.member.dto.SendConfirmationEmailRequest;
import cloneproject.Instagram.domain.member.dto.UpdatePasswordRequest;
import cloneproject.Instagram.domain.member.service.MemberAuthService;
import cloneproject.Instagram.global.error.exception.FilterMustRespondException;
import cloneproject.Instagram.global.result.ResultResponse;
import cloneproject.Instagram.global.util.RequestExtractor;

@Slf4j
@Api(tags = "멤버 인증 API")
@Validated
@RestController
@RequiredArgsConstructor
public class MemberAuthController {

	private final MemberAuthService memberAuthService;
	private final int REFRESH_TOKEN_EXPIRES = 60 * 60 * 24 * 7; // 7일

	@Value("${server-domain}")
	private String SERVER_DOMAIN;

	@ApiOperation(value = "username 중복 조회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "M011 - 사용가능한 username 입니다.\n"
			+ "M012 - 사용불가능한 username 입니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.")
	})
	@ApiImplicitParam(name = "username", value = "유저네임", required = true, example = "dlwlrma")
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
	@ApiResponses({
		@ApiResponse(code = 200, message = "M001 - 회원가입에 성공하였습니다.\n"
			+ "M013 - 이메일 인증을 완료할 수 없습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "M002 - 이미 존재하는 사용자 이름입니다.\n"
			+ "M007 - 인증 이메일 전송을 먼저 해야합니다.")
	})
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
	@ApiResponses({
		@ApiResponse(code = 200, message = "M014 - 인증코드 이메일을 전송하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "M002 - 이미 존재하는 사용자 이름입니다.")
	})
	@PostMapping(value = "/accounts/email")
	public ResponseEntity<ResultResponse> sendConfirmEmail(
		@Valid @RequestBody SendConfirmationEmailRequest sendConfirmationEmailRequest) {
		memberAuthService.sendEmailConfirmation(sendConfirmationEmailRequest.getUsername(),
			sendConfirmationEmailRequest.getEmail());

		return ResponseEntity.ok(ResultResponse.of(SEND_CONFIRM_EMAIL_SUCCESS));
	}

	@ApiOperation(value = "로그인")
	@ApiResponses({
		@ApiResponse(code = 200, message = "M002 - 로그인에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다."),
		@ApiResponse(code = 401, message = "M005 - 계정 정보가 일치하지 않습니다.")
	})
	@PostMapping(value = "/login")
	public ResponseEntity<ResultResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
		throw new FilterMustRespondException();
	}

	@ApiOperation(value = "토큰 재발급")
	@ApiResponses({
		@ApiResponse(code = 200, message = "M003 - 재발급에 성공하였습니다."),
		@ApiResponse(code = 401, message = "J001 - 유효하지 않은 토큰입니다.\n"
			+ "J002 - 만료된 토큰입니다.")
	})
	@PostMapping(value = "/reissue")
	public ResponseEntity<ResultResponse> reissue(
		@CookieValue(value = "refreshToken", required = false) Cookie refreshCookie) {
		throw new FilterMustRespondException();
	}

	@ApiOperation(value = "비밀번호 변경")
	@ApiResponses({
		@ApiResponse(code = 200, message = "M010 - 회원 비밀번호를 변경하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "M013 - 기존 비밀번호와 동일하게 변경할 수 없습니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.\n"
			+ "M005 - 계정 정보가 일치하지 않습니다."),
	})
	@PutMapping(value = "/accounts/password")
	public ResponseEntity<ResultResponse> updatePassword(
		@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest) {
		memberAuthService.updatePassword(updatePasswordRequest);

		return ResponseEntity.ok(ResultResponse.of(UPDATE_PASSWORD_SUCCESS));
	}

	@ApiOperation(value = "비밀번호변경 이메일 전송")
	@ApiResponses({
		@ApiResponse(code = 200, message = "M017 - 비밀번호 재설정 메일을 전송했습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "M001 - 존재 하지 않는 유저입니다.")
	})
	@ApiImplicitParam(name = "username", value = "유저네임", required = true, example = "dlwlrma")
	@PostMapping(value = "/accounts/password/email")
	public ResponseEntity<ResultResponse> sendResetPasswordCode(
		@RequestParam @Length(min = 4, max = 12, message = "사용자 이름은 4문자 이상 12문자 이하여야 합니다")
		@Pattern(regexp = "^[0-9a-zA-Z]+$", message = "username엔 대소문자, 숫자만 사용할 수 있습니다.") String username) {
		final String email = memberAuthService.sendResetPasswordCode(username);

		return ResponseEntity.ok(ResultResponse.of(SEND_RESET_PASSWORD_EMAIL_SUCCESS, email));
	}

	@ApiOperation(value = "코드를 통한 비밀번호 재설정")
	@ApiResponses({
		@ApiResponse(code = 200, message = "M018 - 비밀번호 재설정에 성공했습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "M001 - 존재 하지 않는 유저입니다.\n"
			+ "M007 - 인증 이메일 전송을 먼저 해야합니다.\n"
			+ "M008 - 잘못되거나 만료된 코드입니다.\n"
			+ "M013 - 기존 비밀번호와 동일하게 변경할 수 없습니다.")
	})
	@PutMapping(value = "/accounts/password/reset")
	public ResponseEntity<ResultResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest,
		HttpServletRequest request,
		HttpServletResponse response) {
		final String device = RequestExtractor.getDevice(request);
		final String ip = RequestExtractor.getClientIP(request);
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
	@ApiResponses({
		@ApiResponse(code = 200, message = "M019 - 비밀번호 재설정 코드로 로그인 했습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "M001 - 존재 하지 않는 유저입니다.\n"
			+ "M007 - 인증 이메일 전송을 먼저 해야합니다.\n"
			+ "M008 - 잘못되거나 만료된 코드입니다."),
		@ApiResponse(code = 401, message = "M005 - 계정 정보가 일치하지 않습니다.")
	})
	@PostMapping(value = "/login/recovery")
	public ResponseEntity<ResultResponse> loginWithCode(
		@Valid @RequestBody LoginWithCodeRequest loginWithCodeRequest) {
		throw new FilterMustRespondException();
	}

	@ApiOperation(value = "로그아웃")
	@ApiResponses({
		@ApiResponse(code = 200, message = "M020 - 로그아웃하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.\n"
			+ "J001 - 유효하지 않은 토큰입니다.")
	})
	@PostMapping(value = "/logout")
	public ResponseEntity<ResultResponse> logout(
		@CookieValue(value = "refreshToken", required = true) Cookie refreshCookie,
		HttpServletResponse response) {
		memberAuthService.logout(refreshCookie.getValue());

		final Cookie cookie = new Cookie("refreshToken", null);

		cookie.setMaxAge(0);

		// cookie.setSecure(true); https 미지원
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setDomain(SERVER_DOMAIN);

		response.addCookie(cookie);

		return ResponseEntity.ok(ResultResponse.of(LOGOUT_SUCCESS));
	}

	@ApiOperation(value = "토큰 없이 로그아웃(쿠키만 초기화)")
	@PostMapping(value = "/logout/only/cookie")
	@ApiResponses({
		@ApiResponse(code = 200, message = "M020 - 로그아웃하였습니다.")
	})
	public ResponseEntity<ResultResponse> deleteRefreshToken(HttpServletResponse response) {
		final Cookie cookie = new Cookie("refreshToken", null);

		cookie.setMaxAge(0);

		// cookie.setSecure(true); https 미지원
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setDomain(SERVER_DOMAIN);

		response.addCookie(cookie);

		return ResponseEntity.ok(ResultResponse.of(LOGOUT_SUCCESS));
	}

	@ApiOperation(value = "비밀번호 재설정 코드 검증")
	@ApiResponses({
		@ApiResponse(code = 200, message = "M021 - 올바른 비밀번호 재설정 코드입니다.\n"
			+ "M022 - 올바르지 않은 비밀번호 재설정 코드입니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "M007 - 인증 이메일 전송을 먼저 해야합니다.")
	})
	@ApiImplicitParams({
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
	@ApiResponses({
		@ApiResponse(code = 200, message = "M024 - 로그인 한 기기들을 조회하였습니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.\n")
	})
	@GetMapping(value = "/accounts/login/device")
	public ResponseEntity<ResultResponse> getLoginDevices(
		@CookieValue(value = "refreshToken", required = true) Cookie refreshCookie) {
		final List<LoginDeviceDto> loginDevicesDtos = memberAuthService.getLoginDevices(refreshCookie.getValue());

		return ResponseEntity.ok(ResultResponse.of(GET_LOGIN_DEVICES_SUCCESS, loginDevicesDtos));
	}

	@ApiOperation(value = "기기 로그아웃 시키기")
	@ApiResponses({
		@ApiResponse(code = 200, message = "M025 - 해당 기기를 로그아웃 시켰습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "M007 - 인증 이메일 전송을 먼저 해야합니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.\n"
			+ "J001 - 유효하지 않은 토큰입니다.")
	})
	@ApiImplicitParam(name = "tokenId", value = "로그아웃시킬 기기의 tokenId", required = true,
		example = "aaaaaa-aaaa-aaaa-aaaa-39f9a58c8c3d")
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
		cookie.setDomain(SERVER_DOMAIN);

		return cookie;
	}

}
