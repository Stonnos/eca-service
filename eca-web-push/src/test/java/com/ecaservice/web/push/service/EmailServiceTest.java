package com.ecaservice.web.push.service;

import com.ecaservice.core.lock.config.CoreLockAutoConfiguration;
import com.ecaservice.core.lock.metrics.LockMeterService;
import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.web.push.AbstractJpaTest;
import com.ecaservice.web.push.TestHelperUtils;
import com.ecaservice.web.push.config.AppProperties;
import com.ecaservice.web.push.config.EncryptConfiguration;
import com.ecaservice.web.push.config.MailProperties;
import com.ecaservice.web.push.entity.Email;
import com.ecaservice.web.push.entity.TemplateEntity;
import com.ecaservice.web.push.exception.DuplicateRequestIdException;
import com.ecaservice.web.push.mapping.EmailRequestMapperImpl;
import com.ecaservice.web.push.repository.EmailRepository;
import com.ecaservice.web.push.repository.TemplateRepository;
import com.ecaservice.web.push.service.template.TemplateProcessorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static com.ecaservice.web.push.TestHelperUtils.createTemplateEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link EmailService} functionality.
 *
 * @author Roman Batygin
 */
@EnableAspectJAutoProxy
@Import({EmailRequestMapperImpl.class, EncryptConfiguration.class, MailProperties.class,
        EmailService.class, CoreLockAutoConfiguration.class, AppProperties.class})
class EmailServiceTest extends AbstractJpaTest {

    private static final String EMAIL_MESSAGE = "message";

    @MockBean
    private TemplateProcessorService templateProcessorService;
    @MockBean
    private TemplateRepository templateRepository;
    @MockBean
    private LockMeterService lockMeterService;
    @Autowired
    private EmailRepository emailRepository;

    @Autowired
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
        assertThat(email).isNotNull();
        assertThat(email.getUuid()).isNotNull();
        assertThat(email.getSubject()).isEqualTo(templateEntity.getSubject());
        assertThat(emailRepository.existsById(email.getId())).isTrue();
    }

    @Test
    void testSaveEmailShouldThrowDuplicateEventIdException() {
        Email email = emailService.saveEmail(emailRequest);
        assertThat(email).isNotNull();
        assertThrows(DuplicateRequestIdException.class, () -> emailService.saveEmail(emailRequest));
    }

    private void prepareTestEmailRequest() {
        emailRequest = TestHelperUtils.createEmailRequest();
        templateEntity = createTemplateEntity();
        when(templateRepository.findByCode(templateEntity.getCode())).thenReturn(Optional.of(templateEntity));
        when(templateProcessorService.process(templateEntity.getCode(), emailRequest.getVariables()))
                .thenReturn(EMAIL_MESSAGE);
    }
}
