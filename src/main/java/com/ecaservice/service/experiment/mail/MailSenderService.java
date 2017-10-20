package com.ecaservice.service.experiment.mail;

import com.ecaservice.mapping.EmailMapper;
import com.ecaservice.model.Mail;
import com.ecaservice.model.entity.Email;
import com.ecaservice.repository.EmailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
    private final EmailMapper emailMapper;
    private final EmailRepository emailRepository;

    /**
     * Constructor with dependency spring injection.
     * @param mailSender {@link JavaMailSender} bean
     * @param emailMapper {@link EmailMapper} bean
     * @param emailRepository {@link EmailRepository} bean
     */
    @Autowired
    public MailSenderService(JavaMailSender mailSender, EmailMapper emailMapper,
                             EmailRepository emailRepository) {
        this.mailSender = mailSender;
        this.emailMapper = emailMapper;
        this.emailRepository = emailRepository;
    }

    /**
     * Sends email.
     *
     * @param mail {@link Mail} object
     */
    public void sendEmail(Mail mail) throws MailException, MessagingException {
        Assert.notNull(mail, "Mail is not specified!");
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom(mail.getSender());
        messageHelper.setTo(mail.getReceiver());
        messageHelper.setSubject(mail.getSubject());
        messageHelper.setText(mail.getMessage(), mail.isHtml());
        mailSender.send(message);
        Email email = emailMapper.map(mail);
        email.setSaveDate(LocalDateTime.now());
        emailRepository.save(email);
    }
}
