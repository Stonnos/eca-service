package com.ecaservice.core.mail.client.service;

import com.ecaservice.common.web.crypto.EncryptorBase64AdapterService;
import com.ecaservice.core.mail.client.config.EcaMailClientProperties;
import com.ecaservice.core.mail.client.entity.EmailRequestEntity;
import com.ecaservice.core.mail.client.event.model.EmailEvent;
import com.ecaservice.core.mail.client.mapping.EmailRequestMapperImpl;
import com.ecaservice.notification.dto.EmailRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.core.mail.client.TestHelperUtils.createEmailRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link EmailEventService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({EmailEventService.class, EmailRequestMapperImpl.class, EcaMailClientProperties.class})
class EmailEventServiceTest {

    private static final long REQUEST_CACHE_DURATION_IN_MINUTES = 30L;

    @Inject
    private EmailEventService emailEventService;

    @MockBean
    private EmailRequestSender emailRequestSender;
    @MockBean
    private EncryptorBase64AdapterService encryptorBase64AdapterService;

    @Test
    void testHandleEmailEvent() {
        var emailRequest = createEmailRequest();
        var emailEvent = new EmailEvent(this, emailRequest);
        emailEvent.setRequestCacheDurationInMinutes(REQUEST_CACHE_DURATION_IN_MINUTES);
        emailEventService.handleEmailEvent(emailEvent);
        verify(emailRequestSender, atLeastOnce()).sendEmail(any(EmailRequest.class), any(EmailRequestEntity.class));
    }
}
