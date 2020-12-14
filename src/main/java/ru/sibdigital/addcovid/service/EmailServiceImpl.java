package ru.sibdigital.addcovid.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.sibdigital.addcovid.model.MailingStatuses;
import ru.sibdigital.addcovid.model.RegMailingHistory;
import ru.sibdigital.addcovid.repository.RegMailingHistoryRepo;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.Timestamp;
import java.util.Map;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {


    @Autowired
    private RegMailingHistoryRepo regMailingHistoryRepo;

    @Value("${addcovid.from.email}")
    private String from;

    @Autowired
    public JavaMailSender javaMailSender;

    @Async
    public void sendSimpleMessage(String to, String subject, String text) {
        Short sendStatus = MailingStatuses.EMAIL_SENT.value();
        RegMailingHistory history = new RegMailingHistory();
        history.setTimeSend(new Timestamp(System.currentTimeMillis()));
        history.setEmail(to);
        try {

            MimeMessage message = prepareMimeMessage(to, subject, text);
            javaMailSender.send(message);
            history.setStatus(sendStatus);
        } catch (AddressException e) {
            log.error(e.getMessage());
            sendStatus = MailingStatuses.INVALID_ADDRESS.value();
        } catch (MessagingException e) {
            log.error(e.getMessage());
            sendStatus = MailingStatuses.EMAIL_NOT_CREATED.value();
        }catch (MailException e) {
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
            log.error(e.getMessage());
            sendStatus = MailingStatuses.EMAIL_NOT_SENT.value();
        }
        regMailingHistoryRepo.save(history);
    }

    private MimeMessage prepareMimeMessage(String email, String subject, String text) throws MessagingException {
        InternetAddress address = new InternetAddress(email);
        MimeMessage message = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(address.getAddress());

        helper.setSubject(subject);
        helper.setFrom(from);
        helper.setText(text, true);

        return message;
    }
}
