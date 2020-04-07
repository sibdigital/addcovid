package ru.sibdigital.addcovid.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import ru.sibdigital.addcovid.controller.DocRequestController;

@Component
public class EmailServiceImpl implements EmailService {

    @Autowired
    public JavaMailSender javaMailSender;

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom("rabota@govrb.ru");
            message.setSubject(subject);
            message.setText(text);

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
