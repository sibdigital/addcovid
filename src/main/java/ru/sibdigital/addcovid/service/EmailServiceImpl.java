package ru.sibdigital.addcovid.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Value("${addcovid.from.email}")
    private String from;

    @Autowired
    public JavaMailSender javaMailSender;

    @Async
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
//            message.setFrom(from);

            javaMailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
            log.error(e.getMessage());

            log.info(e.getLocalizedMessage());
            log.info(e.getMessage());
        }
    }
}
