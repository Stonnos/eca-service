package com.notification.service;

import com.ecaservice.notification.dto.EmailRequest;
import com.notification.AbstractJpaTest;
import com.notification.TestHelperUtils;
import com.notification.config.MailConfig;
import com.notification.mapping.EmailRequestMapper;
import com.notification.mapping.EmailRequestMapperImpl;
import com.notification.model.Email;
import com.notification.repository.EmailRepository;
import com.notification.service.template.TemplateEngineService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link EmailService} functionality.
 *
 * @author Roman Batygin
 */
@Import(EmailRequestMapperImpl.class)
public class EmailServiceTest extends AbstractJpaTest {

    @Mock
    private MailConfig mailConfig;
    @Mock
    private TemplateEngineService templateEngineService;
    @Inject
    private EmailRepository emailRepository;
    @Inject
    private EmailRequestMapper emailRequestMapper;

    private EmailService emailService;

    @Override
    public void init() {
        emailService = new EmailService(mailConfig, emailRequestMapper, templateEngineService, emailRepository);
    }

    @Override
    public void deleteAll() {
        emailRepository.deleteAll();
    }

    @Test
    void testEmailSaving() {
        EmailRequest emailRequest = TestHelperUtils.createEmailRequest();
        Email email = emailService.saveEmail(emailRequest);
        Assertions.assertThat(email).isNotNull();
        Assertions.assertThat(email.getUuid()).isNotNull();
        Assertions.assertThat(emailRepository.existsById(email.getId()));
    }
}
