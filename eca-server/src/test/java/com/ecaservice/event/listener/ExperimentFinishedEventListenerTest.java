package com.ecaservice.event.listener;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.event.model.ExperimentFinishedEvent;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.service.ers.ErsService;
import com.ecaservice.service.experiment.ExperimentResultsService;
import eca.converters.model.ExperimentHistory;
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
public class ExperimentFinishedEventListenerTest {

    @Mock
    private ErsService ersService;
    @Mock
    private ExperimentResultsService experimentResultsService;
    @InjectMocks
    private ExperimentFinishedEventListener experimentFinishedEventListener;

    @Test
    void testExperimentFinishedEvent() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        ExperimentHistory experimentHistory = TestHelperUtils.createExperimentHistory();
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
