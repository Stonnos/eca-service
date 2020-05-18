package com.notification.scheduler;

import com.notification.AbstractJpaTest;
import com.notification.config.MailConfig;
import com.notification.model.Email;
import com.notification.model.EmailStatus;
import com.notification.repository.EmailRepository;
import com.notification.service.MailSenderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import javax.mail.MessagingException;
import java.time.LocalDateTime;

import static com.notification.TestHelperUtils.createEmail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for checking {@link MailScheduler} functionality.
 */
@Import(MailConfig.class)
public class MailSchedulerTest extends AbstractJpaTest {

    @Inject
    private MailConfig mailConfig;
    @Inject
    private EmailRepository emailRepository;
    @Mock
    private MailSenderService mailSenderService;

    private MailScheduler mailScheduler;

    @Override
    public void init() {
        mailScheduler = new MailScheduler(mailConfig, mailSenderService, emailRepository);
    }

    @Override
    public void deleteAll() {
        emailRepository.deleteAll();
    }

    @Test
    public void testEmailSending() throws MessagingException {
        Email email = createEmail(LocalDateTime.now(), EmailStatus.NEW);
        emailRepository.save(email);
        Email email1 = createEmail(LocalDateTime.now().minusHours(1L), EmailStatus.NOT_SENT);
        emailRepository.save(email1);
        Email email2 = createEmail(LocalDateTime.now().minusHours(2L), EmailStatus.SENT);
        email2.setSentDate(LocalDateTime.now().minusHours(1L));
        emailRepository.save(email2);
        Email email3 = createEmail(LocalDateTime.now().minusHours(3L), EmailStatus.EXCEEDED);
        emailRepository.save(email3);
        Email email4 = createEmail(LocalDateTime.now().minusHours(4L), EmailStatus.NOT_SENT);
        email4.setFailedAttemptsToSent(mailConfig.getMaxFailedAttemptsToSent());
        emailRepository.save(email4);
        doThrow(new MessagingException()).when(mailSenderService).sendEmail(email4);
        mailScheduler.sendEmails();
        email = emailRepository.findById(email.getId()).orElse(null);
        assertThat(email).isNotNull();
        assertThat(email.getStatus()).isEqualTo(EmailStatus.SENT);
        assertThat(email.getSentDate()).isNotNull();
        email1 = emailRepository.findById(email1.getId()).orElse(null);
        assertThat(email1).isNotNull();
        assertThat(email1.getStatus()).isEqualTo(EmailStatus.SENT);
        assertThat(email1.getSentDate()).isNotNull();
        email4 = emailRepository.findById(email4.getId()).orElse(null);
        assertThat(email4).isNotNull();
        assertThat(email4.getStatus()).isEqualTo(EmailStatus.EXCEEDED);
        assertThat(email4.getSentDate()).isNull();
        verify(mailSenderService, times(3)).sendEmail(any(Email.class));
    }

    @Test
    public void testNotSentEmail() throws MessagingException {
        Email email = createEmail(LocalDateTime.now(), EmailStatus.NEW);
        emailRepository.save(email);
        doThrow(new MessagingException()).when(mailSenderService).sendEmail(email);
        mailScheduler.sendEmails();
        Email actual = emailRepository.findById(email.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getStatus()).isEqualTo(EmailStatus.NOT_SENT);
    }
}
