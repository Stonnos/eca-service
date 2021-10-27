package com.ecaservice.core.mail.client.service;

import com.ecaservice.core.mail.client.AbstractJpaTest;
import com.ecaservice.core.mail.client.entity.EmailRequestStatus;
import com.ecaservice.core.mail.client.repository.EmailRequestRepository;
import com.ecaservice.notification.dto.EmailResponse;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import java.util.UUID;

import static com.ecaservice.core.mail.client.TestHelperUtils.createEmailRequest;
import static com.ecaservice.core.mail.client.TestHelperUtils.createEmailRequestEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link EmailRequestSender} class.
 *
 * @author Roman Batygin
 */
@Import(EmailRequestSender.class)
class EmailRequestSenderTest extends AbstractJpaTest {

    @Inject
    private EmailRequestSender emailRequestSender;
    @Inject
    private EmailRequestRepository emailRequestRepository;

    @MockBean
    private EmailClient emailClient;

    @Override
    public void deleteAll() {
        emailRequestRepository.deleteAll();
    }

    @Test
    void testSuccessSent() {
        var emailRequest = createEmailRequest();
        var emailRequestEntity = createEmailRequestEntity();
        EmailResponse emailResponse = new EmailResponse();
        emailResponse.setRequestId(UUID.randomUUID().toString());
        when(emailClient.sendEmail(emailRequest)).thenReturn(emailResponse);
        emailRequestSender.sendEmail(emailRequest, emailRequestEntity);
        var actual = emailRequestRepository.findById(emailRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getRequestStatus()).isEqualTo(EmailRequestStatus.SENT);
        assertThat(actual.getSentDate()).isNotNull();
        assertThat(actual.getRequestId()).isEqualTo(emailResponse.getRequestId());
    }

    @Test
    void testNotSent() {
        var emailRequest = createEmailRequest();
        var emailRequestEntity = createEmailRequestEntity();
        doThrow(FeignException.ServiceUnavailable.class).when(emailClient).sendEmail(emailRequest);
        emailRequestSender.sendEmail(emailRequest, emailRequestEntity);
        var actual = emailRequestRepository.findById(emailRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getRequestStatus()).isEqualTo(EmailRequestStatus.NOT_SENT);
        assertThat(actual.getSentDate()).isNull();
    }

    @Test
    void testErrorSent() {
        var emailRequest = createEmailRequest();
        var emailRequestEntity = createEmailRequestEntity();
        doThrow(FeignException.BadRequest.class).when(emailClient).sendEmail(emailRequest);
        emailRequestSender.sendEmail(emailRequest, emailRequestEntity);
        var actual = emailRequestRepository.findById(emailRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getRequestStatus()).isEqualTo(EmailRequestStatus.ERROR);
        assertThat(actual.getSentDate()).isNull();
    }
}
