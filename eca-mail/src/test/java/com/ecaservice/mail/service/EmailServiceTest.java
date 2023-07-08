package com.ecaservice.mail.service;

import com.ecaservice.core.lock.annotation.EnableLocks;
import com.ecaservice.core.lock.metrics.LockMeterService;
import com.ecaservice.mail.AbstractJpaTest;
import com.ecaservice.mail.TestHelperUtils;
import com.ecaservice.mail.config.EncryptorConfiguration;
import com.ecaservice.mail.config.MailConfig;
import com.ecaservice.mail.exception.DuplicateRequestIdException;
import com.ecaservice.mail.mapping.EmailRequestMapperImpl;
import com.ecaservice.mail.model.Email;
import com.ecaservice.mail.model.TemplateEntity;
import com.ecaservice.mail.repository.EmailRepository;
import com.ecaservice.mail.repository.TemplateRepository;
import com.ecaservice.mail.service.template.TemplateProcessorService;
import com.ecaservice.notification.dto.EmailRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.ecaservice.mail.TestHelperUtils.createTemplateEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link EmailService} functionality.
 *
 * @author Roman Batygin
 */
@EnableAspectJAutoProxy
@EnableLocks
@Import({EmailRequestMapperImpl.class, EncryptorConfiguration.class, MailConfig.class, EmailService.class})
class EmailServiceTest extends AbstractJpaTest {

    private static final String EMAIL_MESSAGE = "message";
    private static final int NUM_THREADS = 2;

    @MockBean
    private TemplateProcessorService templateProcessorService;
    @MockBean
    private TemplateRepository templateRepository;
    @MockBean
    private LockMeterService lockMeterService;
    @Inject
    private EmailRepository emailRepository;

    @Inject
    private EmailService emailService;

    private EmailRequest emailRequest;
    private TemplateEntity templateEntity;

    @Override
    public void init() {
        prepareTestEmailRequest();
    }

    @Override
    public void deleteAll() {
        emailRepository.deleteAll();
    }

    @Test
    void testEmailSaving() {
        Email email = emailService.saveEmail(emailRequest);
        Assertions.assertThat(email).isNotNull();
        Assertions.assertThat(email.getUuid()).isNotNull();
        Assertions.assertThat(email.getSubject()).isEqualTo(templateEntity.getSubject());
        Assertions.assertThat(emailRepository.existsById(email.getId())).isTrue();
    }

    @Test
    void testSaveEmailShouldThrowDuplicateEventIdException() {
        Email email = emailService.saveEmail(emailRequest);
        assertThat(email).isNotNull();
        assertThrows(DuplicateRequestIdException.class, () -> emailService.saveEmail(emailRequest));
    }

    @Test
    void testDuplicateRequestIdInMultiThreadEnvironment() throws Exception {
        var hasDuplicateRequestIdError = new AtomicBoolean();
        final CountDownLatch finishedLatch = new CountDownLatch(NUM_THREADS);
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.submit(() -> {
                try {
                    emailService.saveEmail(emailRequest);
                } catch (DuplicateRequestIdException ex) {
                    hasDuplicateRequestIdError.set(true);
                } finally {
                    finishedLatch.countDown();
                }
            });
        }
        finishedLatch.await();
        executorService.shutdownNow();
        assertThat(hasDuplicateRequestIdError.get()).isTrue();
        assertThat(emailRepository.count()).isOne();
    }

    private void prepareTestEmailRequest() {
        emailRequest = TestHelperUtils.createEmailRequest();
        templateEntity = createTemplateEntity();
        when(templateRepository.findByCode(templateEntity.getCode())).thenReturn(Optional.of(templateEntity));
        when(templateProcessorService.process(templateEntity.getCode(), emailRequest.getVariables()))
                .thenReturn(EMAIL_MESSAGE);
    }
}
