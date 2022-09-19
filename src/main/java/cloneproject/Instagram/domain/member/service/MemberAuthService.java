package cloneproject.Instagram.domain.member.service;

import static cloneproject.Instagram.global.error.ErrorCode.*;

import java.util.List;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import cloneproject.Instagram.domain.member.dto.JwtDto;
import cloneproject.Instagram.domain.member.dto.LoginDevicesDto;
import cloneproject.Instagram.domain.member.dto.RegisterRequest;
import cloneproject.Instagram.domain.member.dto.ResetPasswordRequest;
import cloneproject.Instagram.domain.member.dto.UpdatePasswordRequest;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.exception.AccountMismatchException;
import cloneproject.Instagram.domain.member.exception.PasswordResetFailException;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.domain.search.entity.SearchMember;
import cloneproject.Instagram.domain.search.repository.SearchMemberRepository;
import cloneproject.Instagram.global.error.exception.EntityAlreadyExistException;
import cloneproject.Instagram.global.error.exception.EntityNotFoundException;
import cloneproject.Instagram.global.util.AuthUtil;
import cloneproject.Instagram.global.util.JwtUtil;
import cloneproject.Instagram.infra.location.LocationService;
import cloneproject.Instagram.infra.location.dto.Location;

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
	private final LocationService locationService;
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

		if (!emailCodeService.checkRegisterCode(username, registerRequest.getEmail(), registerRequest.getCode())) {
			return false;
		}

		final Member member = convertRegisterRequestToMember(registerRequest);
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
		emailCodeService.sendRegisterCode(username, email);
	}

	@Transactional
	public void updatePassword(UpdatePasswordRequest updatePasswordRequest) {
		final Member member = authUtil.getLoginMember();
		if (!bCryptPasswordEncoder.matches(updatePasswordRequest.getOldPassword(), member.getPassword())) {
			throw new AccountMismatchException();
		}
		if (updatePasswordRequest.getOldPassword().equals(updatePasswordRequest.getNewPassword())) {
			throw new EntityAlreadyExistException(PASSWORD_ALREADY_EXIST);
		}
		final String encryptedPassword = bCryptPasswordEncoder.encode(updatePasswordRequest.getNewPassword());
		member.setEncryptedPassword(encryptedPassword);
		memberRepository.save(member);
	}

	@Transactional
	public String sendResetPasswordCode(String username) {
		return emailCodeService.sendResetPasswordCode(username);
	}

	// TODO 수정
	@Transactional
	public JwtDto resetPassword(ResetPasswordRequest resetPasswordRequest, String device, String ip) {
		final Member member = memberRepository.findByUsername(resetPasswordRequest.getUsername())
			.orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));

		if (!emailCodeService.checkResetPasswordCode(resetPasswordRequest.getUsername(),
			resetPasswordRequest.getCode())) {
			throw new PasswordResetFailException();
		}
		if (bCryptPasswordEncoder.matches(resetPasswordRequest.getNewPassword(), member.getPassword())) {
			throw new EntityAlreadyExistException(PASSWORD_ALREADY_EXIST);
		}

		final String encryptedPassword = bCryptPasswordEncoder.encode(resetPasswordRequest.getNewPassword());
		member.setEncryptedPassword(encryptedPassword);
		memberRepository.save(member);

		final JwtDto jwtDto = jwtUtil.generateJwtDto(member);
		final Location location = locationService.getLocation(ip);

		refreshTokenService.addRefreshToken(member.getId(), jwtDto.getRefreshToken(), device, location);
		emailCodeService.deleteResetPasswordCode(resetPasswordRequest.getUsername());

		return jwtDto;
	}

	@Transactional
	public boolean checkResetPasswordCode(String username, String code) {
		return emailCodeService.checkResetPasswordCode(username, code);
	}

	@Transactional
	public void logout(String refreshToken) {
		refreshTokenService.deleteRefreshTokenWithValue(authUtil.getLoginMemberId(), refreshToken);
	}

	public List<LoginDevicesDto> getLoginDevices(String currentToken) {
		final Member member = authUtil.getLoginMember();
		return refreshTokenService.getLoginDevices(member.getId(), currentToken);
	}

	@Transactional
	public void logoutDevice(String tokenId) {
		refreshTokenService.deleteRefreshTokenWithId(authUtil.getLoginMemberId(), tokenId);
	}

	private Member convertRegisterRequestToMember(RegisterRequest registerRequest) {
		return Member.builder()
			.username(registerRequest.getUsername())
			.name(registerRequest.getName())
			.password(registerRequest.getPassword())
			.email(registerRequest.getEmail())
			.build();
	}

}
