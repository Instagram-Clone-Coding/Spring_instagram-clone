package cloneproject.Instagram.domain.member.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Random;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.entity.redis.EmailCode;
import cloneproject.Instagram.domain.member.entity.redis.ResetPasswordCode;
import cloneproject.Instagram.domain.member.exception.CantResetPasswordException;
import cloneproject.Instagram.domain.member.exception.MemberDoesNotExistException;
import cloneproject.Instagram.domain.member.exception.NoConfirmEmailException;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.domain.member.repository.redis.EmailCodeRedisRepository;
import cloneproject.Instagram.domain.member.repository.redis.ResetPasswordCodeRedisRepository;
import cloneproject.Instagram.global.error.exception.CantSendEmailException;
import cloneproject.Instagram.infra.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor 
@Service
public class EmailCodeService {
    
    private final MemberRepository memberRepository;
    private final EmailCodeRedisRepository emailCodeRedisRepository;
    private final ResetPasswordCodeRedisRepository resetPasswordCodeRedisRepository;
    private final EmailService emailService;

    public boolean sendEmailConfirmationCode(String username, String email){
        String text;
        String code = createConfirmationCode(6);
        try{
            ClassPathResource resource = new ClassPathResource("confirmEmailUI.html");
            String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            text = String.format(html, email, code, email);
        }catch(IOException e){
            throw new CantSendEmailException();
        }
        EmailCode emailCode = EmailCode.builder()
                                        .username(username)
                                        .email(email)
                                        .code(code)
                                        .build();
        emailCodeRedisRepository.save(emailCode);

        emailService.sendHtmlTextEmail(username+ ", welcome to Instagram." ,text, email);
        
        return true;
    }

    public boolean checkEmailCode(String username, String email, String code){
        Optional<EmailCode> optionalEmailCode = emailCodeRedisRepository.findByUsername(username);

        if(optionalEmailCode.isEmpty()){
            throw new NoConfirmEmailException();
        }

        EmailCode emailCode = optionalEmailCode.get();

        if(!emailCode.getCode().equals(code) || !emailCode.getEmail().equals(email)){
            return false;
        }

        emailCodeRedisRepository.delete(emailCode);
        return true;
    }

    public void sendResetPasswordCode(String username){
        Member member = memberRepository.findByUsername(username).orElseThrow(MemberDoesNotExistException::new);
        String code = createConfirmationCode(30);

        ResetPasswordCode resetPasswordCode = ResetPasswordCode.builder()
            .username(username)
            .code(code)
            .build();
        
        resetPasswordCodeRedisRepository.save(resetPasswordCode);

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        String emailText = username + "의 인증코드 " + code;

        mailMessage.setTo(member.getEmail());
        mailMessage.setSubject("비밀번호 재설정 메일입니다");
        mailMessage.setText(emailText);
        emailService.sendEmail(mailMessage);
    }
    
    public boolean checkResetPasswordCode(String username, String code){
        Optional<ResetPasswordCode> optionalResetPasswordCode = resetPasswordCodeRedisRepository.findByUsername(username);

        if(optionalResetPasswordCode.isEmpty()){
            throw new CantResetPasswordException();
        }

        ResetPasswordCode resetPasswordCode = optionalResetPasswordCode.get();

        if(!resetPasswordCode.getCode().equals(code)){
            return false;
        }
        return true;
    }

    public void deleteResetPasswordCode(String username){
        ResetPasswordCode resetPasswordCode = resetPasswordCodeRedisRepository.findByUsername(username)
            .orElseThrow(CantResetPasswordException::new);
        resetPasswordCodeRedisRepository.delete(resetPasswordCode);
    }

    public String createConfirmationCode(int length){
        StringBuffer key = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            boolean isAlphabet = random.nextBoolean();
 
            if(isAlphabet){
                key.append((char) ((int) (random.nextInt(26)) + 65));
            }else{
                key.append((random.nextInt(10)));
            }
        }
        return key.toString();
    }

}