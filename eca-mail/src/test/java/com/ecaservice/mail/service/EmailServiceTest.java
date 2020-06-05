package com.ecaservice.mail.service;

import com.ecaservice.mail.AbstractJpaTest;
import com.ecaservice.mail.TestHelperUtils;
import com.ecaservice.mail.config.MailConfig;
import com.ecaservice.mail.mapping.EmailRequestMapper;
import com.ecaservice.mail.mapping.EmailRequestMapperImpl;
import com.ecaservice.mail.model.Email;
import com.ecaservice.mail.service.template.TemplateEngineService;
import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.mail.repository.EmailRepository;
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
class EmailServiceTest extends AbstractJpaTest {

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
        Assertions.assertThat(emailRepository.existsById(email.getId())).isTrue();
    }
}
