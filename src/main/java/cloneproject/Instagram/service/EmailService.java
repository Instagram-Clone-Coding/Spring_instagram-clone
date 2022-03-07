package cloneproject.Instagram.service;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import cloneproject.Instagram.exception.CantSendEmailException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender javaMailSender;

    @Async
    public void sendEmail(SimpleMailMessage email){
        javaMailSender.send(email);
    }

    @Async
    public void sendHtmlTextEmail(String subject, String content, String email){
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
            messageHelper.setTo(email);
            messageHelper.setSubject(subject);
            messageHelper.setText(content, true);
            javaMailSender.send(message);
        }catch(Exception e){
            throw new CantSendEmailException();
        }
    }

}
