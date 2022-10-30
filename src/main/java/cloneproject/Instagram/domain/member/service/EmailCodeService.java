package cloneproject.Instagram.domain.member.service;

import static cloneproject.Instagram.global.error.ErrorCode.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.entity.redis.RegisterCode;
import cloneproject.Instagram.domain.member.entity.redis.ResetPasswordCode;
import cloneproject.Instagram.domain.member.exception.EmailNotConfirmedException;
import cloneproject.Instagram.domain.member.exception.PasswordResetFailException;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.domain.member.repository.redis.RegisterCodeRedisRepository;
import cloneproject.Instagram.domain.member.repository.redis.ResetPasswordCodeRedisRepository;
import cloneproject.Instagram.global.error.exception.EntityNotFoundException;
import cloneproject.Instagram.global.error.exception.FileConvertFailException;
import cloneproject.Instagram.infra.email.EmailService;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailCodeService {

	private static final int REGISTER_CODE_LENGTH = 6;
	private static final int RESET_PASSWORD_CODE_LENGTH = 30;
	private static final String REGISTER_EMAIL_SUBJECT_POSTFIX = ", Welcome to Instagram.";
	private static final String RESET_PASSWORD_EMAIL_SUBJECT_POSTFIX = ", recover your account's password.";

	private final MemberRepository memberRepository;
	private final RegisterCodeRedisRepository emailCodeRedisRepository;
	private final ResetPasswordCodeRedisRepository resetPasswordCodeRedisRepository;
	private final EmailService emailService;

	private String confirmEmailUI;
	private String resetPasswordEmailUI;

	public void sendRegisterCode(String username, String email) {
		final String code = createConfirmationCode(REGISTER_CODE_LENGTH);
		emailService.sendHtmlTextEmail(username + REGISTER_EMAIL_SUBJECT_POSTFIX, getRegisterEmailText(email, code),
			email);

		final RegisterCode registerCode = RegisterCode.builder()
			.username(username)
			.email(email)
			.code(code)
			.build();
		emailCodeRedisRepository.save(registerCode);
	}

	public boolean checkRegisterCode(String username, String email, String code) {
		final RegisterCode registerCode = emailCodeRedisRepository.findByUsername(username)
			.orElseThrow(EmailNotConfirmedException::new);

		if (!registerCode.getCode().equals(code) || !registerCode.getEmail().equals(email)) {
			return false;
		}

		emailCodeRedisRepository.delete(registerCode);
		return true;
	}

	public String sendResetPasswordCode(String username) {
		final Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));

		final String code = createConfirmationCode(RESET_PASSWORD_CODE_LENGTH);
		final String email = member.getEmail();
		final String text = getResetPasswordEmailText(username, email, code);
		emailService.sendHtmlTextEmail(username + RESET_PASSWORD_EMAIL_SUBJECT_POSTFIX, text, email);

		final ResetPasswordCode resetPasswordCode = ResetPasswordCode.builder()
			.username(username)
			.code(code)
			.build();
		resetPasswordCodeRedisRepository.save(resetPasswordCode);

		return email;
	}

	public boolean checkResetPasswordCode(String username, String code) {
		final ResetPasswordCode resetPasswordCode = resetPasswordCodeRedisRepository
			.findByUsername(username).orElseThrow(EmailNotConfirmedException::new);
		return resetPasswordCode.getCode().equals(code);
	}

	public void deleteResetPasswordCode(String username) {
		final ResetPasswordCode resetPasswordCode = resetPasswordCodeRedisRepository.findByUsername(username)
			.orElseThrow(PasswordResetFailException::new);
		resetPasswordCodeRedisRepository.delete(resetPasswordCode);
	}

	private String getRegisterEmailText(String email, String code) {
		return String.format(confirmEmailUI, email, code, email);
	}

	private String getResetPasswordEmailText(String username, String email, String code) {
		return String.format(resetPasswordEmailUI, username, username, code, username, username, code, email, username);
	}

	private String createConfirmationCode(int length) {
		return RandomStringUtils.random(length, true, true);
	}

	@PostConstruct
	private void loadEmailUI() {
		try {
			final ClassPathResource confirmEmailUIResource = new ClassPathResource("confirmEmailUI.html");
			final ClassPathResource resetPasswordEmailUIResource = new ClassPathResource("resetPasswordEmailUI.html");

			confirmEmailUI = new String(confirmEmailUIResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
			resetPasswordEmailUI = new String(resetPasswordEmailUIResource.getInputStream().readAllBytes(),
				StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new FileConvertFailException();
		}
	}

}
