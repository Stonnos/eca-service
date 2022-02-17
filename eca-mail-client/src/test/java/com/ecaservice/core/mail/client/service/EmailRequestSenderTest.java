package com.ecaservice.core.mail.client.service;

import com.ecaservice.notification.dto.EmailResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ecaservice.core.mail.client.TestHelperUtils.createEmailRequest;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link EmailRequestSender} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class EmailRequestSenderTest {

    @Mock
    private EmailClient emailClient;

    @InjectMocks
    private EmailRequestSender emailRequestSender;

    @Test
    void testHandleEmailEvent() {
        var emailRequest = createEmailRequest();
        when(emailClient.sendEmail(emailRequest)).thenReturn(new EmailResponse());
        emailRequestSender.sendEmail(emailRequest);
        verify(emailClient, atLeastOnce()).sendEmail(emailRequest);
    }
}
