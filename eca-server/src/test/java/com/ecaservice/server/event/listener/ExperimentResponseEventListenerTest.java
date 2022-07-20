package com.ecaservice.server.event.listener;

import com.ecaservice.base.model.ExperimentResponse;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.event.model.ExperimentResponseEvent;
import com.ecaservice.server.mapping.EcaResponseMapper;
import com.ecaservice.server.mapping.EcaResponseMapperImpl;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createExperiment;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ExperimentResponseEventListener} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(EcaResponseMapperImpl.class)
class ExperimentResponseEventListenerTest {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String EXPERIMENT_DOWNLOAD_URL = "http://localhost:9000/experiment";

    @Mock
    private ExperimentConfig experimentConfig;
    @Mock
    private AmqpAdmin amqpAdmin;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Inject
    private EcaResponseMapper ecaResponseMapper;

    @Captor
    private ArgumentCaptor<String> replyToCaptor;
    @Captor
    private ArgumentCaptor<ExperimentResponse> experimentResponseArgumentCaptor;

    private ExperimentResponseEventListener experimentResponseEventListener;

    @BeforeEach
    void init() {
        when(experimentConfig.getDownloadBaseUrl()).thenReturn(BASE_URL);
        experimentResponseEventListener =
                new ExperimentResponseEventListener(amqpAdmin, rabbitTemplate, ecaResponseMapper);
    }

    @Test
    void testHandleExperimentResponse() {
        Experiment experiment = createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experiment.setExperimentDownloadUrl(EXPERIMENT_DOWNLOAD_URL);
        ExperimentResponseEvent experimentResponseEvent = new ExperimentResponseEvent(this, experiment);
        when(amqpAdmin.getQueueInfo(experiment.getReplyTo()))
                .thenReturn(new QueueInformation(experiment.getReplyTo(), 0, 0));
        experimentResponseEventListener.handleExperimentResponseEvent(experimentResponseEvent);
        verify(rabbitTemplate).convertAndSend(replyToCaptor.capture(), experimentResponseArgumentCaptor.capture(),
                any(MessagePostProcessor.class));
        assertThat(replyToCaptor.getValue()).isEqualTo(experiment.getReplyTo());
        ExperimentResponse actualResponse = experimentResponseArgumentCaptor.getValue();
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getRequestId()).isEqualTo(experiment.getRequestId());
        assertThat(actualResponse.getStatus()).isEqualTo(TechnicalStatus.SUCCESS);
        assertThat(actualResponse.getDownloadUrl()).isEqualTo(experiment.getExperimentDownloadUrl());
    }
}
