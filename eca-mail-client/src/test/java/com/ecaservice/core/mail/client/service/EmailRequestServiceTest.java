package com.ecaservice.core.mail.client.service;

import com.ecaservice.common.web.crypto.EncryptorBase64AdapterService;
import com.ecaservice.core.mail.client.AbstractJpaTest;
import com.ecaservice.core.mail.client.config.EcaMailClientProperties;
import com.ecaservice.core.mail.client.entity.EmailRequestEntity;
import com.ecaservice.core.mail.client.entity.EmailRequestStatus;
import com.ecaservice.core.mail.client.mapping.EmailRequestMapperImpl;
import com.ecaservice.core.mail.client.repository.EmailRequestRepository;
import com.ecaservice.notification.dto.EmailRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Arrays;

import static com.ecaservice.core.mail.client.TestHelperUtils.createEmailRequestEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link EmailRequestService} class.
 *
 * @author Roman Batygin
 */
@Import({EmailRequestService.class, EmailRequestMapperImpl.class, EcaMailClientProperties.class})
class EmailRequestServiceTest extends AbstractJpaTest {

    private static final String INVALID_REQUEST_JSON = "abc";

    @Inject
    private EmailRequestRepository emailRequestRepository;
    @Inject
    private EmailRequestService emailRequestService;

    @MockBean
    private EmailRequestSender emailRequestSender;
    @MockBean
    private EncryptorBase64AdapterService encryptorBase64AdapterService;

    @Override
    public void deleteAll() {
        emailRequestRepository.deleteAll();
    }

    @Test
    void testProcessNotSentRequests() {
        var first = createEmailRequestEntity(EmailRequestStatus.SENT, null);
        var second = createEmailRequestEntity(EmailRequestStatus.NOT_SENT, null);
        var third = createEmailRequestEntity(EmailRequestStatus.ERROR, null);
        var forth = createEmailRequestEntity(EmailRequestStatus.EXCEEDED, null);
        var fifth = createEmailRequestEntity(EmailRequestStatus.NOT_SENT, LocalDateTime.now().minusMinutes(1L));
        emailRequestRepository.saveAll(Arrays.asList(first, second, third, forth, fifth));
        emailRequestService.processNotSentEmailRequests();
        verify(emailRequestSender, atLeastOnce()).sendEmail(any(EmailRequest.class),
                any(EmailRequestEntity.class));
    }

    @Test
    void testProcessNotSentEventWithError() {
        var emailRequestEntity = createEmailRequestEntity(EmailRequestStatus.NOT_SENT, null);
        emailRequestEntity.setRequestJson(INVALID_REQUEST_JSON);
        emailRequestRepository.save(emailRequestEntity);
        emailRequestService.processNotSentEmailRequests();
        verify(emailRequestSender, never()).sendEmail(any(EmailRequest.class),
                any(EmailRequestEntity.class));
        var actual = emailRequestRepository.findById(emailRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getRequestStatus()).isEqualTo(EmailRequestStatus.ERROR);
    }

    @Test
    void testProcessExceededRequests() {
        var first = createEmailRequestEntity(EmailRequestStatus.SENT, null);
        var second = createEmailRequestEntity(EmailRequestStatus.NOT_SENT, null);
        var third = createEmailRequestEntity(EmailRequestStatus.ERROR, null);
        var forth = createEmailRequestEntity(EmailRequestStatus.EXCEEDED, null);
        var fifth = createEmailRequestEntity(EmailRequestStatus.NOT_SENT, LocalDateTime.now().minusMinutes(1L));
        emailRequestRepository.saveAll(Arrays.asList(first, second, third, forth, fifth));
        emailRequestService.processExceededEmailRequests();
        var actual = emailRequestRepository.findById(fifth.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getRequestStatus()).isEqualTo(EmailRequestStatus.EXCEEDED);
    }
}
