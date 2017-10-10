package com.ecaservice.service.experiment;

import com.ecaservice.model.Mail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Email sender service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class MailSenderService {

    private final JavaMailSender mailSender;

    @Autowired
    public MailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends email.
     *
     * @param mail {@link Mail} object
     */
    public void sendEmail(Mail mail) throws MailException, MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom(mail.getFrom());
        messageHelper.setTo(mail.getTo());
        messageHelper.setSubject(mail.getSubject());
        messageHelper.setText(mail.getMessage(), mail.isHtml());
        mailSender.send(message);
    }
}
