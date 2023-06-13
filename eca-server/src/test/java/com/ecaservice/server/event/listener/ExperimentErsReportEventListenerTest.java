package com.ecaservice.server.event.listener;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.event.model.ExperimentErsReportEvent;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import com.ecaservice.server.model.entity.RequestStatus;
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
class ExperimentErsReportEventListenerTest {

    @Mock
    private ErsService ersService;
    @Mock
    private ExperimentResultsService experimentResultsService;
    @InjectMocks
    private ExperimentErsReportEventListener experimentErsReportEventListener;

    @Test
    void testExperimentFinishedEvent() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        AbstractExperiment experimentHistory = TestHelperUtils.createExperimentHistory();
        ExperimentErsReportEvent experimentErsReportEvent =
                new ExperimentErsReportEvent(this, experiment, experimentHistory);
        ExperimentResultsEntity experimentResultsEntity = TestHelperUtils.createExperimentResultsEntity(experiment);
        when(experimentResultsService.saveExperimentResultsToErsSent(experiment, experimentHistory)).thenReturn(
                Collections.singletonList(experimentResultsEntity));
        experimentErsReportEventListener.handleEvent(experimentErsReportEvent);
        verify(ersService, atLeastOnce()).sentExperimentResults(experimentResultsEntity, experimentHistory);
    }
}
