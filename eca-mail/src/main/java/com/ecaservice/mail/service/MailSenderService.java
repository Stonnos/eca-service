package com.ecaservice.mail.service;

import com.ecaservice.common.web.crypto.EncryptorBase64AdapterService;
import com.ecaservice.mail.model.Email;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Email sender service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailSenderService {

    private final EncryptorBase64AdapterService encryptorBase64AdapterService;
    private final JavaMailSender mailSender;

    /**
     * Sends email.
     *
     * @param email - email object
     */
    public void sendEmail(Email email) throws MessagingException {
        log.info("Starting to send email message [{}] with subject [{}] from '{}' to '{}'.", email.getUuid(),
                email.getSubject(), email.getSender(), email.getReceiver());
        MimeMessage message = buildMimeMessage(email);
        mailSender.send(message);
        log.info("Email message [{}] with subject [{}] has been sent from '{}' to '{}'.", email.getUuid(),
                email.getSubject(), email.getSender(), email.getReceiver());
    }

    private MimeMessage buildMimeMessage(Email email) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom(email.getSender());
        messageHelper.setTo(email.getReceiver());
        messageHelper.setSubject(email.getSubject());
        String decodedMessage = encryptorBase64AdapterService.decrypt(email.getMessage());
        messageHelper.setText(decodedMessage, true);
        return message;
    }
}
