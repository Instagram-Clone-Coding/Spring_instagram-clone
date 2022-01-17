package cloneproject.Instagram.service;

import java.util.Optional;
import java.util.Random;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import cloneproject.Instagram.entity.redis.EmailCode;
import cloneproject.Instagram.repository.EmailCodeRedisRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Service
public class EmailCodeService {

    
    private final EmailCodeRedisRepository emailCodeRedisRepository;
    private final EmailService emailService;

    public boolean sendEmailConfirmationCode(String username, String email){

        String code = createConfirmationCode();

        EmailCode emailCode = EmailCode.builder()
                                        .username(username)
                                        .email(email)
                                        .code(code)
                                        .build();
        emailCodeRedisRepository.save(emailCode);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("회원가입 이메일 인증코드");
        mailMessage.setText(code);
        emailService.sendEmail(mailMessage);
        
        return true;
    }

    public EmailCode findByUsername(String username){
        Optional<EmailCode> emailCode = emailCodeRedisRepository.findByUsername(username);
        return emailCode.orElseThrow(null);
    }

    public void deleteEmailCode(EmailCode emailCode){
        emailCodeRedisRepository.delete(emailCode);
    }
    
    public String createConfirmationCode(){
        StringBuffer key = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
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