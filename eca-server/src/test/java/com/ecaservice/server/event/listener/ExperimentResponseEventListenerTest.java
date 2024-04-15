package com.ecaservice.server.event.listener;

import com.ecaservice.base.model.ExperimentResponse;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.server.event.model.ExperimentResponseEvent;
import com.ecaservice.server.mapping.EcaResponseMapper;
import com.ecaservice.server.mapping.EcaResponseMapperImpl;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.service.EcaResponseSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createExperiment;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for checking {@link ExperimentResponseEventListener} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(EcaResponseMapperImpl.class)
class ExperimentResponseEventListenerTest {

    private static final String EXPERIMENT_DOWNLOAD_URL = "http://localhost:9000/experiment";

    @Mock
    private EcaResponseSender ecaResponseSender;
    @Autowired
    private EcaResponseMapper ecaResponseMapper;

    @Captor
    private ArgumentCaptor<String> replyToCaptor;
    @Captor
    private ArgumentCaptor<String> correlationIdCaptor;
    @Captor
    private ArgumentCaptor<ExperimentResponse> experimentResponseArgumentCaptor;

    private ExperimentResponseEventListener experimentResponseEventListener;

    @BeforeEach
    void init() {
        experimentResponseEventListener =
                new ExperimentResponseEventListener(ecaResponseSender, ecaResponseMapper);
    }

    @Test
    void testHandleExperimentResponse() {
        Experiment experiment = createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experiment.setExperimentDownloadUrl(EXPERIMENT_DOWNLOAD_URL);
        ExperimentResponseEvent experimentResponseEvent = new ExperimentResponseEvent(this, experiment);
        experimentResponseEventListener.handleExperimentResponseEvent(experimentResponseEvent);
        verify(ecaResponseSender).sendResponse(experimentResponseArgumentCaptor.capture(),
                correlationIdCaptor.capture(), replyToCaptor.capture());
        assertThat(replyToCaptor.getValue()).isEqualTo(experiment.getReplyTo());
        assertThat(correlationIdCaptor.getValue()).isEqualTo(experiment.getCorrelationId());
        ExperimentResponse actualResponse = experimentResponseArgumentCaptor.getValue();
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getRequestId()).isEqualTo(experiment.getRequestId());
        assertThat(actualResponse.getStatus()).isEqualTo(TechnicalStatus.SUCCESS);
        assertThat(actualResponse.getDownloadUrl()).isEqualTo(experiment.getExperimentDownloadUrl());
    }
}
