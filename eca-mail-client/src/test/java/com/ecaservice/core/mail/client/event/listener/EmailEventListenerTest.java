package com.ecaservice.core.mail.client.event.listener;

import com.ecaservice.core.mail.client.TestEmailEvent;
import com.ecaservice.core.mail.client.TestEmailEventHandler;
import com.ecaservice.core.mail.client.service.SimpleEmailRequestSender;
import com.ecaservice.notification.dto.EmailRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link EmailEventListener} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class EmailEventListenerTest {

    private EmailEventListener emailEventListener;

    @Mock
    private SimpleEmailRequestSender simpleEmailRequestSender;

    @Captor
    private ArgumentCaptor<EmailRequest> emailRequestArgumentCaptor;

    private TestEmailEventHandler testEmailEventHandler = new TestEmailEventHandler();

    @BeforeEach
    void init() {
        emailEventListener = new EmailEventListener(simpleEmailRequestSender,
                Collections.singletonList(testEmailEventHandler));
    }

    @Test
    void testHandleEmailEvent() {
        var testEmailEvent = new TestEmailEvent(this);
        emailEventListener.handleEmailEvent(testEmailEvent);
        verify(simpleEmailRequestSender, atLeastOnce()).sendEmail(emailRequestArgumentCaptor.capture());
        var emailRequest = emailRequestArgumentCaptor.getValue();
        assertThat(emailRequest).isNotNull();
        assertThat(emailRequest.getRequestId()).isNotNull();
        assertThat(emailRequest.getTemplateCode()).isEqualTo(testEmailEventHandler.getTemplateCode(testEmailEvent));
    }
}
