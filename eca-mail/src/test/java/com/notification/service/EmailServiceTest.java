package com.notification.service;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailResponse;
import com.notification.AbstractJpaTest;
import com.notification.TestHelperUtils;
import com.notification.mapping.EmailRequestMapper;
import com.notification.mapping.EmailRequestMapperImpl;
import com.notification.model.Email;
import com.notification.repository.EmailRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.List;

/**
 * Unit tests for checking {@link EmailService} functionality.
 *
 * @author Roman Batygin
 */
@Import(EmailRequestMapperImpl.class)
public class EmailServiceTest extends AbstractJpaTest {

    @Inject
    private EmailRepository emailRepository;
    @Inject
    private EmailRequestMapper emailRequestMapper;

    private EmailService emailService;

    @Override
    public void init() {
        emailService = new EmailService(emailRepository, emailRequestMapper);
    }

    @Override
    public void deleteAll() {
        emailRepository.deleteAll();
    }

    @Test
    public void testEmailSaving() {
        EmailRequest emailRequest = TestHelperUtils.createEmailRequest();
        EmailResponse emailResponse = emailService.saveEmail(emailRequest);
        Assertions.assertThat(emailResponse).isNotNull();
        Assertions.assertThat(emailResponse.getRequestId()).isNotNull();
        List<Email> emails = emailRepository.findAll();
        Assertions.assertThat(emails).hasSize(1);
    }
}
