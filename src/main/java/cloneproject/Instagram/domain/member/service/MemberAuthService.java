package cloneproject.Instagram.domain.member.service;

import static cloneproject.Instagram.global.error.ErrorCode.*;

import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloneproject.Instagram.domain.member.dto.JwtDto;
import cloneproject.Instagram.domain.member.dto.LoginRequest;
import cloneproject.Instagram.domain.member.dto.LoginWithCodeRequest;
import cloneproject.Instagram.domain.member.dto.LoginedDevicesDTO;
import cloneproject.Instagram.domain.member.dto.RegisterRequest;
import cloneproject.Instagram.domain.member.dto.ResetPasswordRequest;
import cloneproject.Instagram.domain.member.dto.UpdatePasswordRequest;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.entity.redis.RefreshToken;
import cloneproject.Instagram.domain.member.exception.AccountMismatchException;
import cloneproject.Instagram.domain.member.exception.InvalidJwtException;
import cloneproject.Instagram.domain.member.exception.PasswordResetFailException;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.domain.search.entity.SearchMember;
import cloneproject.Instagram.domain.search.repository.SearchMemberRepository;
import cloneproject.Instagram.global.error.exception.EntityAlreadyExistException;
import cloneproject.Instagram.global.error.exception.EntityNotFoundException;
import cloneproject.Instagram.global.util.AuthUtil;
import cloneproject.Instagram.global.util.JwtUtil;
import cloneproject.Instagram.infra.geoip.GeoIPLocationService;
import cloneproject.Instagram.infra.geoip.dto.GeoIP;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberAuthService {

	private final AuthUtil authUtil;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final JwtUtil jwtUtil;
	private final EmailCodeService emailCodeService;
	private final MemberRepository memberRepository;
	private final RefreshTokenService refreshTokenService;
	private final GeoIPLocationService geoIPLocationService;
	private final SearchMemberRepository searchMemberRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Transactional(readOnly = true)
	public boolean checkUsername(String username) {
		return !memberRepository.existsByUsername(username);
	}

	@Transactional
	public boolean register(RegisterRequest registerRequest) {
		if (memberRepository.existsByUsername(registerRequest.getUsername())) {
			throw new EntityAlreadyExistException(USERNAME_ALREADY_EXIST);
		}
		final String username = registerRequest.getUsername();

		if (!emailCodeService.checkEmailCode(username, registerRequest.getEmail(), registerRequest.getCode())) {
			return false;
		}

		final Member member = registerRequest.convert();
		final String encryptedPassword = bCryptPasswordEncoder.encode(member.getPassword());
		member.setEncryptedPassword(encryptedPassword);
		memberRepository.save(member);

		final SearchMember searchMember = new SearchMember(member);
		searchMemberRepository.save(searchMember);

		return true;
	}

	public void sendEmailConfirmation(String username, String email) {
		if (memberRepository.existsByUsername(username)) {
			throw new EntityAlreadyExistException(USERNAME_ALREADY_EXIST);
		}
		emailCodeService.sendEmailConfirmationCode(username, email);
	}

	@Transactional
	public JwtDto login(LoginRequest loginRequest, String device, String ip) {
		try {
			final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				loginRequest.getUsername(), loginRequest.getPassword());
			final Authentication authentication = authenticationManagerBuilder.getObject()
				.authenticate(authenticationToken);
			final JwtDto jwtDto = jwtUtil.generateTokenDto(authentication);

			final GeoIP geoIP = geoIPLocationService.getLocation(ip);

			refreshTokenService.addRefreshToken(Long.valueOf(authentication.getName()), jwtDto.getRefreshToken(),
				device, geoIP);
			return jwtDto;
		} catch (BadCredentialsException e) {
			throw new AccountMismatchException();
		}
	}

	@Transactional
	public Optional<JwtDto> reissue(String refreshTokenString) {
		if (!jwtUtil.validateRefeshJwt(refreshTokenString)) {
			throw new InvalidJwtException();
		}
		Authentication authentication;
		try {
			authentication = jwtUtil.getAuthentication(refreshTokenString, false);
		} catch (JwtException e) {
			throw new InvalidJwtException();
		}
		final Member member = authUtil.getLoginMember();

		final Optional<RefreshToken> refreshToken = refreshTokenService.findRefreshToken(member.getId(),
			refreshTokenString);
		if (refreshToken.isEmpty()) {
			return Optional.empty();
		}
		final JwtDto jwtDto = jwtUtil.generateTokenDto(authentication);
		refreshTokenService.updateRefreshToken(refreshToken.get(), jwtDto.getRefreshToken());
		return Optional.of(jwtDto);
	}

	@Transactional
	public void updatePassword(UpdatePasswordRequest updatePasswordRequest) {
		final Member member = authUtil.getLoginMember();
		final boolean oldPasswordCorrect = bCryptPasswordEncoder.matches(updatePasswordRequest.getOldPassword(),
			member.getPassword());
		if (!oldPasswordCorrect) {
			throw new AccountMismatchException();
		}
		final String encryptedPassword = bCryptPasswordEncoder.encode(updatePasswordRequest.getNewPassword());
		member.setEncryptedPassword(encryptedPassword);
		memberRepository.save(member);
	}

	@Transactional
	public String sendResetPasswordCode(String username) {
		return emailCodeService.sendResetPasswordCode(username);
	}

	@Transactional
	public JwtDto resetPassword(ResetPasswordRequest resetPasswordRequest, String device, String ip) {
		final Member member = memberRepository.findByUsername(resetPasswordRequest.getUsername())
			.orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));
		if (!emailCodeService.checkResetPasswordCode(resetPasswordRequest.getUsername(),
			resetPasswordRequest.getCode())) {
			throw new PasswordResetFailException();
		}
		final String encryptedPassword = bCryptPasswordEncoder.encode(resetPasswordRequest.getNewPassword());
		member.setEncryptedPassword(encryptedPassword);
		memberRepository.save(member);
		final JwtDto jwtDto = login(
			new LoginRequest(resetPasswordRequest.getUsername(), resetPasswordRequest.getNewPassword()), device,
			ip);
		emailCodeService.deleteResetPasswordCode(resetPasswordRequest.getUsername());
		return jwtDto;
	}

	@Transactional
	public JwtDto loginWithCode(LoginWithCodeRequest loginRequest, String device, String ip) {
		final Member member = memberRepository.findByUsername(loginRequest.getUsername())
			.orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));
		if (!emailCodeService.checkResetPasswordCode(loginRequest.getUsername(), loginRequest.getCode())) {
			throw new PasswordResetFailException();
		}
		final JwtDto jwtDto = jwtUtil.generateTokenDto(jwtUtil.getAuthenticationWithMember(member.getId().toString()));
		emailCodeService.deleteResetPasswordCode(loginRequest.getUsername());

		final GeoIP geoIP = geoIPLocationService.getLocation(ip);

		refreshTokenService.addRefreshToken(member.getId(), jwtDto.getRefreshToken(), device, geoIP);
		return jwtDto;
	}

	@Transactional
	public boolean checkResetPasswordCode(String username, String code) {
		return emailCodeService.checkResetPasswordCode(username, code);
	}

	@Transactional
	public void logout(String refreshToken) {
		try {
			refreshTokenService.deleteRefreshTokenWithValue(authUtil.getLoginMemberId(), refreshToken);
		} catch (InvalidJwtException ignored) {

		}
	}

	public List<LoginedDevicesDTO> getLoginDevices() {
		final Member member = authUtil.getLoginMember();
		return refreshTokenService.getLoginDevices(member.getId());
	}

	@Transactional
	public void logoutDevice(String tokenId) {
		refreshTokenService.deleteRefreshTokenWithId(authUtil.getLoginMemberId(), tokenId);
	}

}
