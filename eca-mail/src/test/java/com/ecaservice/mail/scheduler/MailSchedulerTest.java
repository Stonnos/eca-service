package com.ecaservice.mail.scheduler;

import com.ecaservice.mail.AbstractJpaTest;
import com.ecaservice.mail.TestHelperUtils;
import com.ecaservice.mail.config.MailConfig;
import com.ecaservice.mail.metrics.MetricsService;
import com.ecaservice.mail.model.Email;
import com.ecaservice.mail.model.EmailStatus;
import com.ecaservice.mail.repository.EmailRepository;
import com.ecaservice.mail.service.MailSenderService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import javax.mail.MessagingException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for checking {@link MailScheduler} functionality.
 */
@Import(MailConfig.class)
class MailSchedulerTest extends AbstractJpaTest {

    @Inject
    private MailConfig mailConfig;
    @Inject
    private EmailRepository emailRepository;
    @MockBean
    private MailSenderService mailSenderService;
    @MockBean
    private MetricsService metricsService;

    private MailScheduler mailScheduler;

    @Override
    public void init() {
        mailScheduler = new MailScheduler(mailConfig, metricsService, mailSenderService, emailRepository);
    }

    @Override
    public void deleteAll() {
        emailRepository.deleteAll();
    }

    @Test
    void testEmailSending() throws MessagingException {
        Email email = TestHelperUtils.createEmail(LocalDateTime.now(), EmailStatus.NEW);
        emailRepository.save(email);
        Email email1 = TestHelperUtils.createEmail(LocalDateTime.now().minusHours(1L), EmailStatus.NOT_SENT);
        emailRepository.save(email1);
        Email email2 = TestHelperUtils.createEmail(LocalDateTime.now().minusHours(2L), EmailStatus.SENT);
        email2.setSentDate(LocalDateTime.now().minusHours(1L));
        emailRepository.save(email2);
        Email email3 = TestHelperUtils.createEmail(LocalDateTime.now().minusHours(3L), EmailStatus.EXCEEDED);
        emailRepository.save(email3);
        mailScheduler.sendEmails();
        verifyEmailIsSent(email);
        verifyEmailIsSent(email1);
        verify(mailSenderService, times(2)).sendEmail(any(Email.class));
    }

    @Test
    void testNotSentEmail() throws MessagingException {
        Email email = TestHelperUtils.createEmail(LocalDateTime.now(), EmailStatus.NEW);
        emailRepository.save(email);
        doThrow(MessagingException.class).when(mailSenderService).sendEmail(any(Email.class));
        internalTestEmailSent(email, EmailStatus.NOT_SENT);
    }

    @Test
    void testExceededEmail() throws MessagingException {
        Email email = TestHelperUtils.createEmail(LocalDateTime.now(), EmailStatus.NOT_SENT);
        email.setFailedAttemptsToSent(mailConfig.getMaxFailedAttemptsToSent());
        emailRepository.save(email);
        doThrow(MessagingException.class).when(mailSenderService).sendEmail(any(Email.class));
        internalTestEmailSent(email, EmailStatus.EXCEEDED);
    }

    private void internalTestEmailSent(Email email, EmailStatus expectedStatus) {
        mailScheduler.sendEmails();
        verifyEmailStatus(email, expectedStatus);
    }

    private Email verifyEmailStatus(Email email, EmailStatus expectedStatus) {
        Email actual = emailRepository.findById(email.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getStatus()).isEqualTo(expectedStatus);
        return actual;
    }

    private void verifyEmailIsSent(Email email) {
        Email actual = verifyEmailStatus(email, EmailStatus.SENT);
        assertThat(actual.getSentDate()).isNotNull();
    }
}
