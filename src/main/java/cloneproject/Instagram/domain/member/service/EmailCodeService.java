package cloneproject.Instagram.domain.member.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Random;

import javax.annotation.PostConstruct;

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
import cloneproject.Instagram.global.error.exception.CantConvertFileException;
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
    
    private String confirmEmailUI;
    private String resetPasswordEmailUI;

    @PostConstruct
    private void loadEmailUI(){
        try{
            ClassPathResource resource = new ClassPathResource("confirmEmailUI.html");
            confirmEmailUI = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            resource = new ClassPathResource("resetPasswordEmailUI.html");
            resetPasswordEmailUI = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        }catch(IOException e){
            throw new CantConvertFileException();
        }
    }

    public void sendEmailConfirmationCode(String username, String email){
        String code = createConfirmationCode(6);
        String text = String.format(confirmEmailUI, email, code, email);
        emailService.sendHtmlTextEmail(username+ ", Welcome to Instagram." ,text, email);

        EmailCode emailCode = EmailCode.builder()
                                        .username(username)
                                        .email(email)
                                        .code(code)
                                        .build();
        emailCodeRedisRepository.save(emailCode);
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

    public String sendResetPasswordCode(String username){
        Member member = memberRepository.findByUsername(username)
            .orElseThrow(MemberDoesNotExistException::new);

        String code = createConfirmationCode(30);
        String email = member.getEmail();
        String text = String.format(resetPasswordEmailUI, username, code, username, code, email, username);
        emailService.sendHtmlTextEmail(username+ ", recover your account's password." ,text, email);
        
        ResetPasswordCode resetPasswordCode = ResetPasswordCode.builder()
            .username(username)
            .code(code)
            .build();
        resetPasswordCodeRedisRepository.save(resetPasswordCode);

        return email;
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