package com.ecaservice.service.experiment.mail;

import com.ecaservice.exception.EcaServiceException;
import com.ecaservice.model.entity.Email;
import com.ecaservice.repository.EmailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;

/**
 * Email sender service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class MailSenderService {

    private final JavaMailSender mailSender;
    private final EmailRepository emailRepository;

    /**
     * Constructor with dependency spring injection.
     *
     * @param mailSender      {@link JavaMailSender} bean
     * @param emailRepository {@link EmailRepository} bean
     */
    @Inject
    public MailSenderService(JavaMailSender mailSender,
                             EmailRepository emailRepository) {
        this.mailSender = mailSender;
        this.emailRepository = emailRepository;
    }

    /**
     * Sends email.
     *
     * @param email {@link Email} email object
     */
    public void sendEmail(Email email) {
        Assert.notNull(email, "Mail is not specified!");
        log.info("Starting to send email message from '{}' to '{}'.", email.getSender(), email.getReceiver());
        try {
            MimeMessage message = buildMimeMessage(email);
            mailSender.send(message);
            email.setSent(true);
            email.setSentDate(LocalDateTime.now());
            log.info("Email message has been sent from '{}' to '{}'.", email.getSender(), email.getReceiver());
        } catch (Exception ex) {
            log.error("There was an error while sending email [{}]: {} ", email.getId(), ex.getMessage());
            email.setSent(false);
            email.setErrorMessage(ex.getMessage());
            throw new EcaServiceException(ex.getMessage());
        } finally {
            emailRepository.save(email);
        }
    }

    private MimeMessage buildMimeMessage(Email email) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom(email.getSender());
        messageHelper.setTo(email.getReceiver());
        messageHelper.setSubject(email.getSubject());
        messageHelper.setText(email.getMessage(), true);
        return message;
    }
}
