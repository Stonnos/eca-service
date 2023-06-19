package com.ecaservice.server.mq.listener;

import com.ecaservice.base.model.InstancesRequest;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.event.model.EvaluationErsReportEvent;
import com.ecaservice.server.event.model.EvaluationResponseEvent;
import com.ecaservice.server.model.evaluation.EvaluationResultsDataModel;
import com.ecaservice.server.service.evaluation.EvaluationOptimizerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link EvaluationOptimizerRequestListener} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class EvaluationOptimizerRequestListenerTest {

    @Mock
    private EvaluationOptimizerService evaluationOptimizerService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private EvaluationOptimizerRequestListener evaluationOptimizerRequestListener;

    @BeforeEach
    void init() {
        evaluationOptimizerRequestListener =
                new EvaluationOptimizerRequestListener(evaluationOptimizerService, eventPublisher);
    }

    @Test
    void testHandleMessage() {
        InstancesRequest instancesRequest = new InstancesRequest();
        Message message = Mockito.mock(Message.class);
        when(evaluationOptimizerService.evaluateWithOptimalClassifierOptions(instancesRequest))
                .thenReturn(new EvaluationResultsDataModel());
        MessageProperties messageProperties = TestHelperUtils.buildMessageProperties();
        when(message.getMessageProperties()).thenReturn(messageProperties);
        evaluationOptimizerRequestListener.handleMessage(instancesRequest, message);
        verify(eventPublisher, atLeastOnce()).publishEvent(any(EvaluationErsReportEvent.class));
        verify(eventPublisher, atLeastOnce()).publishEvent(any(EvaluationResponseEvent.class));
    }
}
