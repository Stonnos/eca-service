package com.ecaservice.core.mail.client.service;

import com.ecaservice.core.mail.client.TestEmailEvent;
import com.ecaservice.core.mail.client.TestEmailEventHandler;
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
 * Unit tests for {@link SimpleEmailEventProcessor} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class SimpleEmailEventProcessorTest {

    private SimpleEmailEventProcessor simpleEmailEventProcessor;

    @Mock
    private EmailRequestSender emailRequestSender;

    @Captor
    private ArgumentCaptor<EmailRequest> emailRequestArgumentCaptor;

    private TestEmailEventHandler testEmailEventHandler = new TestEmailEventHandler();

    @BeforeEach
    void init() {
        simpleEmailEventProcessor = new SimpleEmailEventProcessor(emailRequestSender,
                Collections.singletonList(testEmailEventHandler));
    }

    @Test
    void testHandleEmailEvent() {
        var testEmailEvent = new TestEmailEvent(this);
        simpleEmailEventProcessor.handleEmailEvent(testEmailEvent);
        verify(emailRequestSender, atLeastOnce()).sendEmail(emailRequestArgumentCaptor.capture());
        var emailRequest = emailRequestArgumentCaptor.getValue();
        assertThat(emailRequest).isNotNull();
        assertThat(emailRequest.getRequestId()).isNotNull();
        assertThat(emailRequest.getTemplateCode()).isEqualTo(testEmailEventHandler.getTemplateCode());
        assertThat(emailRequest.getReceiver()).isEqualTo(testEmailEventHandler.getReceiver());
    }
}
