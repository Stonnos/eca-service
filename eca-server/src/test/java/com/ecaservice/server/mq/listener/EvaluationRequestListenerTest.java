package com.ecaservice.server.mq.listener;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.event.model.EvaluationFinishedEvent;
import com.ecaservice.server.mapping.ClassifierInfoMapperImpl;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.mapping.EvaluationLogMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.evaluation.EvaluationRequestDataModel;
import com.ecaservice.server.service.evaluation.EvaluationRequestService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link EvaluationRequestListener} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({EvaluationRequestListener.class, EvaluationLogMapperImpl.class, InstancesInfoMapperImpl.class,
        ClassifierInfoMapperImpl.class, DateTimeConverter.class})
class EvaluationRequestListenerTest {

    @MockBean
    private RabbitTemplate rabbitTemplate;
    @MockBean
    private EvaluationRequestService evaluationRequestService;
    @MockBean
    private ApplicationEventPublisher eventPublisher;

    @Inject
    private EvaluationLogMapper evaluationLogMapper;

    private EvaluationRequestListener evaluationRequestListener;

    @Captor
    private ArgumentCaptor<String> replyToCaptor;

    @BeforeEach
    void init() {
        evaluationRequestListener =
                new EvaluationRequestListener(rabbitTemplate, evaluationRequestService, eventPublisher,
                        evaluationLogMapper);
    }

    @Test
    void testHandleMessage() {
        EvaluationRequest evaluationRequest = TestHelperUtils.createEvaluationRequest();
        Message message = Mockito.mock(Message.class);
        when(evaluationRequestService.processRequest(any(EvaluationRequestDataModel.class))).thenReturn(
                new EvaluationResponse());
        MessageProperties messageProperties = TestHelperUtils.buildMessageProperties();
        when(message.getMessageProperties()).thenReturn(messageProperties);
        evaluationRequestListener.handleMessage(evaluationRequest, message);
        verify(eventPublisher, atLeastOnce()).publishEvent(any(EvaluationFinishedEvent.class));
        verify(rabbitTemplate).convertAndSend(replyToCaptor.capture(), any(EvaluationResponse.class),
                any(MessagePostProcessor.class));
        Assertions.assertThat(replyToCaptor.getValue()).isEqualTo(messageProperties.getReplyTo());
    }
}
