package com.ecaservice.mail.service;

import com.ecaservice.mail.AbstractJpaTest;
import com.ecaservice.mail.TestHelperUtils;
import com.ecaservice.mail.config.EncryptorConfiguration;
import com.ecaservice.mail.config.MailConfig;
import com.ecaservice.mail.mapping.EmailRequestMapper;
import com.ecaservice.mail.mapping.EmailRequestMapperImpl;
import com.ecaservice.mail.model.Email;
import com.ecaservice.mail.model.TemplateEntity;
import com.ecaservice.mail.repository.EmailRepository;
import com.ecaservice.mail.service.template.TemplateProcessorService;
import com.ecaservice.notification.dto.EmailRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static com.ecaservice.mail.TestHelperUtils.createTemplateEntity;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link EmailService} functionality.
 *
 * @author Roman Batygin
 */
@Import({EmailRequestMapperImpl.class, EncryptorConfiguration.class, MailConfig.class})
class EmailServiceTest extends AbstractJpaTest {

    private static final String EMAIL_MESSAGE = "message";
    private static final String SENDER_MAIL = "sender@mail.ru";

    @Mock
    private MailConfig mailConfig;
    @Mock
    private TemplateProcessorService templateProcessorService;
    @Mock
    private TemplateService templateService;
    @Inject
    private EmailRepository emailRepository;
    @Inject
    private EmailRequestMapper emailRequestMapper;
    @Inject
    private EncryptorBase64AdapterService encryptorBase64AdapterService;

    private EmailService emailService;

    @Override
    public void init() {
        emailService = new EmailService(mailConfig, emailRequestMapper, templateService, templateProcessorService,
                encryptorBase64AdapterService, emailRepository);
    }

    @Override
    public void deleteAll() {
        emailRepository.deleteAll();
    }

    @Test
    void testEmailSaving() {
        EmailRequest emailRequest = TestHelperUtils.createEmailRequest();
        TemplateEntity templateEntity = createTemplateEntity();
        when(mailConfig.getSender()).thenReturn(SENDER_MAIL);
        when(templateService.getTemplate(templateEntity.getCode())).thenReturn(templateEntity);
        when(templateProcessorService.process(templateEntity.getCode(), emailRequest.getVariables()))
                .thenReturn(EMAIL_MESSAGE);
        Email email = emailService.saveEmail(emailRequest);
        Assertions.assertThat(email).isNotNull();
        Assertions.assertThat(email.getUuid()).isNotNull();
        Assertions.assertThat(email.getSubject()).isEqualTo(templateEntity.getSubject());
        Assertions.assertThat(emailRepository.existsById(email.getId())).isTrue();
    }
}
