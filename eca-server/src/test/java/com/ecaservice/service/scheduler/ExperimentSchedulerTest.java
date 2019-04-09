package com.ecaservice.service.scheduler;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.repository.EmailRequestRepository;
import com.ecaservice.repository.ErsRequestRepository;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.repository.ExperimentResultsRequestRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.ers.ErsService;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.service.experiment.mail.NotificationService;
import eca.converters.model.ExperimentHistory;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests that checks ExperimentScheduler functionality {@see ExperimentScheduler}.
 *
 * @author Roman Batygin
 */
@Import(ExperimentConfig.class)
public class ExperimentSchedulerTest extends AbstractJpaTest {

    @Inject
    private ExperimentRepository experimentRepository;
    @Inject
    private ErsRequestRepository ersRequestRepository;
    @Inject
    private EmailRequestRepository emailRequestRepository;
    @Inject
    private ExperimentResultsRequestRepository experimentResultsRequestRepository;
    @Mock
    private ExperimentService experimentService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private ErsService ersService;
    @Captor
    private ArgumentCaptor<Experiment> argumentCaptor;

    @Inject
    private ExperimentConfig experimentConfig;

    private ExperimentScheduler experimentScheduler;

    @Override
    public void init() {
        experimentScheduler = new ExperimentScheduler(experimentRepository, experimentService, notificationService,
                ersService, experimentConfig);
    }

    @Override
    public void deleteAll() {
        ersRequestRepository.deleteAll();
        emailRequestRepository.deleteAll();
        experimentRepository.deleteAll();
    }

    @Test
    public void testProcessExperiments() {
        List<Experiment> experiments = new ArrayList<>();
        experiments.add(TestHelperUtils.createExperiment(UUID.randomUUID().toString()));
        experiments.add(TestHelperUtils.createExperiment(UUID.randomUUID().toString()));
        experimentRepository.saveAll(experiments);
        experimentScheduler.processNewRequests();
        verify(experimentService, times(experiments.size())).processExperiment(any(Experiment.class));
    }

    @Test
    public void testSentExperiments() {
        List<Experiment> experiments = new ArrayList<>();
        experiments.add(TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED));
        experiments.add(TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR));
        experiments.add(TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.TIMEOUT));
        experimentRepository.saveAll(experiments);
        experimentScheduler.processRequestsToSent();
        verify(notificationService, times(experiments.size())).notifyByEmail(any(Experiment.class));
    }

    @Test
    public void testRemoveExperiments() {
        List<Experiment> experiments = new ArrayList<>();
        experiments.add(TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED));
        Experiment experimentToRemove =
                TestHelperUtils.createSentExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR,
                        LocalDateTime.now().minusDays(experimentConfig.getNumberOfDaysForStorage() + 1));
        experiments.add(experimentToRemove);
        experiments.add(TestHelperUtils.createSentExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED,
                LocalDateTime.now()));
        Experiment experiment =
                TestHelperUtils.createSentExperiment(UUID.randomUUID().toString(), RequestStatus.TIMEOUT,
                        LocalDateTime.now());
        experiment.setDeletedDate(LocalDateTime.now());
        experiments.add(experiment);
        experimentRepository.saveAll(experiments);
        experimentScheduler.processRequestsToRemove();
        verify(experimentService).removeExperimentData(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(experimentToRemove);
    }

    @Test
    public void testSentExperimentsToErs() {
        Experiment finishedExperiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        Experiment removedExperiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        removedExperiment.setDeletedDate(LocalDateTime.now());
        Experiment errorExperiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.TIMEOUT);
        Experiment finishedExperimentWithNoOneRequests =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        Experiment finishedExperimentWithErrorRequests =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experimentRepository.saveAll(Arrays.asList(finishedExperiment, removedExperiment, errorExperiment,
                finishedExperimentWithNoOneRequests, finishedExperimentWithErrorRequests));

        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(finishedExperiment, ResponseStatus.SUCCESS));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(finishedExperiment, ResponseStatus.SUCCESS));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(finishedExperimentWithErrorRequests,
                        ResponseStatus.ERROR));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(finishedExperimentWithErrorRequests,
                        ResponseStatus.DUPLICATE_REQUEST_ID));

        when(experimentService.getExperimentResults(anyString())).thenReturn(new ExperimentHistory());
        experimentScheduler.processRequestsToErs();
        verify(ersService, times(2)).sentExperimentHistory(any(Experiment.class), any(ExperimentHistory.class),
                any(ExperimentResultsRequestSource.class));
    }
}
