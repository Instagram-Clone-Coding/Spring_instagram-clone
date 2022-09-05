package cloneproject.Instagram.domain.member.service;

import static cloneproject.Instagram.global.error.ErrorCode.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Random;

import javax.annotation.PostConstruct;

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
import cloneproject.Instagram.global.error.exception.FileConvertFailException;
import cloneproject.Instagram.global.error.exception.EntityNotFoundException;
import cloneproject.Instagram.infra.email.EmailService;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailCodeService {

	private final MemberRepository memberRepository;
	private final RegisterCodeRedisRepository emailCodeRedisRepository;
	private final ResetPasswordCodeRedisRepository resetPasswordCodeRedisRepository;
	private final EmailService emailService;

	private String confirmEmailUI;
	private String resetPasswordEmailUI;

	public void sendRegisterCode(String username, String email) {
		final String code = createConfirmationCode(6);
		final String text = String.format(confirmEmailUI, email, code, email);
		emailService.sendHtmlTextEmail(username + ", Welcome to Instagram.", text, email);

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

		final String code = createConfirmationCode(30);
		final String email = member.getEmail();
		final String text = String.format(resetPasswordEmailUI, username, username, code, username, username, code,
			email, username);
		emailService.sendHtmlTextEmail(username + ", recover your account's password.", text, email);

		final ResetPasswordCode resetPasswordCode = ResetPasswordCode.builder()
			.username(username)
			.code(code)
			.build();
		resetPasswordCodeRedisRepository.save(resetPasswordCode);

		return email;
	}

	public boolean checkResetPasswordCode(String username, String code) {
		final Optional<ResetPasswordCode> optionalResetPasswordCode = resetPasswordCodeRedisRepository
			.findByUsername(username);

		if (optionalResetPasswordCode.isEmpty()) {
			throw new EmailNotConfirmedException();
		}

		final ResetPasswordCode resetPasswordCode = optionalResetPasswordCode.get();

		return resetPasswordCode.getCode().equals(code);
	}

	public void deleteResetPasswordCode(String username) {
		final ResetPasswordCode resetPasswordCode = resetPasswordCodeRedisRepository.findByUsername(username)
			.orElseThrow(PasswordResetFailException::new);
		resetPasswordCodeRedisRepository.delete(resetPasswordCode);
	}

	public String createConfirmationCode(int length) {
		final StringBuilder key = new StringBuilder();
		final Random random = new Random();
		for (int i = 0; i < length; i++) {
			final boolean isAlphabet = random.nextBoolean();

			if (isAlphabet) {
				key.append((char)((random.nextInt(26)) + 65));
			} else {
				key.append((random.nextInt(10)));
			}
		}
		return key.toString();
	}

	@PostConstruct
	private void loadEmailUI() {
		try {
			ClassPathResource resource = new ClassPathResource("confirmEmailUI.html");
			confirmEmailUI = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

			resource = new ClassPathResource("resetPasswordEmailUI.html");
			resetPasswordEmailUI = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new FileConvertFailException();
		}
	}

}
