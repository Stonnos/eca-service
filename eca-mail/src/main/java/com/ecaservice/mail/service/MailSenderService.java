package com.ecaservice.mail.service;

import com.ecaservice.mail.model.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Email sender service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailSenderService {

    private final JavaMailSender mailSender;

    /**
     * Sends email.
     *
     * @param email - email object
     */
    public void sendEmail(Email email) throws MessagingException {
        Assert.notNull(email, "Mail is not specified!");
        log.info("Starting to send email message [{}] from '{}' to '{}'.", email.getUuid(), email.getSender(),
                email.getReceiver());
        MimeMessage message = buildMimeMessage(email);
        mailSender.send(message);
        log.info("Email message [{}] has been sent from '{}' to '{}'.", email.getUuid(), email.getSender(),
                email.getReceiver());
    }

    private MimeMessage buildMimeMessage(Email email) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom(email.getSender());
        messageHelper.setTo(email.getReceiver());
        messageHelper.setSubject(email.getSubject());
        messageHelper.setText(email.getMessage(), email.isHtml());
        return message;
    }
}
