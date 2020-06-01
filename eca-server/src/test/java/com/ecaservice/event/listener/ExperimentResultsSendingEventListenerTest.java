package com.ecaservice.event.listener;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.event.model.ExperimentResultsSendingEvent;
import com.ecaservice.exception.experiment.ResultsNotFoundException;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.repository.ExperimentResultsEntityRepository;
import com.ecaservice.service.ers.ErsService;
import com.ecaservice.service.experiment.ExperimentResultsLockService;
import com.ecaservice.service.experiment.ExperimentService;
import eca.converters.model.ExperimentHistory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@see ExperimentResultsSendingEventListener} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class ExperimentResultsSendingEventListenerTest {

    @Mock
    private ExperimentService experimentService;
    @Mock
    private ErsService ersService;
    @Mock
    private ExperimentResultsLockService lockService;
    @Mock
    private ExperimentResultsEntityRepository experimentResultsEntityRepository;

    @InjectMocks
    private ExperimentResultsSendingEventListener experimentResultsSendingEventListener;

    @Test
    void testExperimentResultsSendingEvent() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        List<ExperimentResultsEntity> experimentResultsEntityList =
                Collections.singletonList(TestHelperUtils.createExperimentResultsEntity(experiment));
        when(experimentResultsEntityRepository.findExperimentsResultsToErsSent(experiment)).thenReturn(
                experimentResultsEntityList);
        when(experimentService.getExperimentHistory(experiment)).thenReturn(new ExperimentHistory());
        experimentResultsSendingEventListener.handleExperimentResultsSendingEvent(
                new ExperimentResultsSendingEvent(this, experiment));
        verify(ersService, times(experimentResultsEntityList.size())).sentExperimentResults(
                any(ExperimentResultsEntity.class), any(ExperimentHistory.class),
                any(ExperimentResultsRequestSource.class));
        verify(lockService, atLeastOnce()).unlock(experiment.getRequestId());
    }

    @Test
    void testExperimentResultsSendingEventWithException() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        List<ExperimentResultsEntity> experimentResultsEntityList =
                Collections.singletonList(TestHelperUtils.createExperimentResultsEntity(experiment));
        when(experimentResultsEntityRepository.findExperimentsResultsToErsSent(experiment)).thenReturn(
                experimentResultsEntityList);
        when(experimentService.getExperimentHistory(experiment)).thenThrow(new ResultsNotFoundException("Error"));
        experimentResultsSendingEventListener.handleExperimentResultsSendingEvent(
                new ExperimentResultsSendingEvent(this, experiment));
        verify(ersService, never()).sentExperimentResults(
                any(ExperimentResultsEntity.class), any(ExperimentHistory.class),
                any(ExperimentResultsRequestSource.class));
        verify(lockService, atLeastOnce()).unlock(experiment.getRequestId());
    }
}
