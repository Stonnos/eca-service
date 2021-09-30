package com.ecaservice.server.event.listener;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.server.event.model.ExperimentFinishedEvent;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.server.service.ers.ErsService;
import com.ecaservice.server.service.experiment.ExperimentResultsService;
import eca.dataminer.AbstractExperiment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@see ExperimentFinishedListener} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class ExperimentFinishedEventListenerTest {

    @Mock
    private ErsService ersService;
    @Mock
    private ExperimentResultsService experimentResultsService;
    @InjectMocks
    private ExperimentFinishedEventListener experimentFinishedEventListener;

    @Test
    void testExperimentFinishedEvent() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        AbstractExperiment experimentHistory = TestHelperUtils.createExperimentHistory();
        ExperimentFinishedEvent experimentFinishedEvent =
                new ExperimentFinishedEvent(this, experiment, experimentHistory);
        ExperimentResultsEntity experimentResultsEntity = TestHelperUtils.createExperimentResultsEntity(experiment);
        when(experimentResultsService.saveExperimentResultsToErsSent(experiment, experimentHistory)).thenReturn(
                Collections.singletonList(experimentResultsEntity));
        experimentFinishedEventListener.handleExperimentFinishedEvent(experimentFinishedEvent);
        verify(ersService, atLeastOnce()).sentExperimentResults(experimentResultsEntity, experimentHistory,
                ExperimentResultsRequestSource.SYSTEM);
    }
}
