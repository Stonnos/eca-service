package com.ecaservice.server.mq.listener;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.event.model.EvaluationErsReportEvent;
import com.ecaservice.server.event.model.EvaluationResponseEvent;
import com.ecaservice.server.mapping.ClassifierInfoMapperImpl;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.mapping.EvaluationLogMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.evaluation.EvaluationRequestDataModel;
import com.ecaservice.server.model.evaluation.EvaluationResultsDataModel;
import com.ecaservice.server.service.evaluation.EvaluationRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
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
    private EvaluationRequestService evaluationRequestService;
    @MockBean
    private ApplicationEventPublisher eventPublisher;

    @Inject
    private EvaluationLogMapper evaluationLogMapper;

    private EvaluationRequestListener evaluationRequestListener;

    @BeforeEach
    void init() {
        evaluationRequestListener = new EvaluationRequestListener(evaluationRequestService, eventPublisher, evaluationLogMapper);
    }

    @Test
    void testHandleMessage() {
        EvaluationRequest evaluationRequest = TestHelperUtils.createEvaluationRequest();
        Message message = Mockito.mock(Message.class);
        when(evaluationRequestService.processRequest(any(EvaluationRequestDataModel.class)))
                .thenReturn(new EvaluationResultsDataModel());
        MessageProperties messageProperties = TestHelperUtils.buildMessageProperties();
        when(message.getMessageProperties()).thenReturn(messageProperties);
        evaluationRequestListener.handleMessage(evaluationRequest, message);
        verify(eventPublisher, atLeastOnce()).publishEvent(any(EvaluationErsReportEvent.class));
        verify(eventPublisher, atLeastOnce()).publishEvent(any(EvaluationResponseEvent.class));
    }
}
