package cloneproject.Instagram.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender javaMailSender;

    @Async
    public void sendEmail(SimpleMailMessage email){
        javaMailSender.send(email);
    }

}
