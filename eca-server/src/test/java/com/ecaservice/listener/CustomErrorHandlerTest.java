package com.ecaservice.listener;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.base.model.EcaResponse;
import com.ecaservice.base.model.MessageError;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.listener.support.AbstractExceptionTranslator;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link CustomErrorHandler} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class CustomErrorHandlerTest {

    private static final String ERROR_MESSAGE = "errorMessage";

    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private List<AbstractExceptionTranslator> exceptionTranslators;;
    @InjectMocks
    private CustomErrorHandler customErrorHandler;

    @Captor
    private ArgumentCaptor<String> replyToCaptor;
    @Captor
    private ArgumentCaptor<EcaResponse> ecaResponseArgumentCaptor;

    @Test
    void testHandleError() {
        Message message = Mockito.mock(Message.class);
        MessageProperties messageProperties = TestHelperUtils.buildMessageProperties();
        when(message.getMessageProperties()).thenReturn(messageProperties);
        customErrorHandler.handleError(
                new ListenerExecutionFailedException(StringUtils.EMPTY, new IllegalStateException(ERROR_MESSAGE),
                        message));
        verify(rabbitTemplate).convertAndSend(replyToCaptor.capture(), ecaResponseArgumentCaptor.capture(),
                any(MessagePostProcessor.class));
        assertThat(replyToCaptor.getValue()).isEqualTo(messageProperties.getReplyTo());
        assertThat(ecaResponseArgumentCaptor.getValue()).isNotNull();
        assertThat(ecaResponseArgumentCaptor.getValue().getStatus()).isEqualTo(TechnicalStatus.ERROR);
        assertThat(ecaResponseArgumentCaptor.getValue().getErrors()).isNotEmpty();
        assertThat(ecaResponseArgumentCaptor.getValue().getErrors()).hasSize(1);
        MessageError error = ecaResponseArgumentCaptor.getValue().getErrors().iterator().next();
        assertThat(error).isNotNull();
        assertThat(error.getMessage()).isEqualTo(ERROR_MESSAGE);
    }
}
