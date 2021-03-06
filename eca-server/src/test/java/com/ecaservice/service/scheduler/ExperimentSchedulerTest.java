package com.ecaservice.service.scheduler;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.CommonConfig;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.event.model.ExperimentEmailEvent;
import com.ecaservice.event.model.ExperimentWebPushEvent;
import com.ecaservice.model.entity.AppInstanceEntity;
import com.ecaservice.model.entity.ErsResponseStatus;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.repository.AppInstanceRepository;
import com.ecaservice.repository.ErsRequestRepository;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.repository.ExperimentResultsEntityRepository;
import com.ecaservice.repository.ExperimentResultsRequestRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.AppInstanceService;
import com.ecaservice.service.ers.ErsService;
import com.ecaservice.service.experiment.ExperimentProgressService;
import com.ecaservice.service.experiment.ExperimentService;
import eca.converters.model.ExperimentHistory;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests that checks ExperimentScheduler functionality {@see ExperimentScheduler}.
 *
 * @author Roman Batygin
 */
@Import({ExperimentConfig.class, CommonConfig.class})
class ExperimentSchedulerTest extends AbstractJpaTest {

    private static final int EXPECTED_CHANGE_STATUS_EVENTS_COUNT = 4;

    @Inject
    private ExperimentRepository experimentRepository;
    @Inject
    private ErsRequestRepository ersRequestRepository;
    @Inject
    private ExperimentResultsRequestRepository experimentResultsRequestRepository;
    @Inject
    private ExperimentResultsEntityRepository experimentResultsEntityRepository;
    @Inject
    private AppInstanceRepository appInstanceRepository;
    @Mock
    private ExperimentService experimentService;
    @Mock
    private ErsService ersService;
    @Mock
    private ExperimentProgressService experimentProgressService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Captor
    private ArgumentCaptor<Experiment> argumentCaptor;

    @Inject
    private ExperimentConfig experimentConfig;
    @Inject
    private CommonConfig commonConfig;

    private ExperimentScheduler experimentScheduler;

    private AppInstanceEntity appInstanceEntity;

    @Override
    public void init() {
        AppInstanceService appInstanceService = new AppInstanceService(commonConfig, appInstanceRepository);
        experimentScheduler =
                new ExperimentScheduler(experimentRepository, experimentResultsEntityRepository, experimentService,
                        eventPublisher, ersService, appInstanceService, experimentProgressService, experimentConfig);
        appInstanceService.saveAppInstance();
        appInstanceEntity = appInstanceService.getAppInstanceEntity();
    }

    @Override
    public void deleteAll() {
        ersRequestRepository.deleteAll();
        experimentResultsEntityRepository.deleteAll();
        experimentRepository.deleteAll();
        appInstanceRepository.deleteAll();
    }

    @Test
    void testProcessExperiments() {
        List<Experiment> experiments = newArrayList();
        experiments.add(
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, appInstanceEntity));
        experiments.add(
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, appInstanceEntity));
        experimentRepository.saveAll(experiments);
        experimentScheduler.processNewRequests();
        verify(experimentService, times(experiments.size())).processExperiment(any(Experiment.class));
        verify(eventPublisher, times(EXPECTED_CHANGE_STATUS_EVENTS_COUNT)).publishEvent(
                any(ExperimentEmailEvent.class));
        verify(eventPublisher, times(EXPECTED_CHANGE_STATUS_EVENTS_COUNT)).publishEvent(
                any(ExperimentWebPushEvent.class));
    }

    @Test
    void testSentExperiments() {
        List<Experiment> experiments = newArrayList();
        experiments.add(TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED,
                appInstanceEntity));
        experiments.add(
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR, appInstanceEntity));
        experiments.add(TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.TIMEOUT,
                appInstanceEntity));
        experimentRepository.saveAll(experiments);
        experimentScheduler.processRequestsToSent();
        verify(eventPublisher, times(experiments.size())).publishEvent(any(ExperimentEmailEvent.class));
    }

    @Test
    void testRemoveExperiments() {
        List<Experiment> experiments = newArrayList();
        experiments.add(TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED,
                appInstanceEntity));
        Experiment experimentToRemove =
                TestHelperUtils.createSentExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR,
                        LocalDateTime.now().minusDays(experimentConfig.getNumberOfDaysForStorage() + 1));
        experimentToRemove.setAppInstanceEntity(appInstanceEntity);
        experiments.add(experimentToRemove);
        Experiment sentExperiment =
                TestHelperUtils.createSentExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED,
                        LocalDateTime.now());
        sentExperiment.setAppInstanceEntity(appInstanceEntity);
        experiments.add(sentExperiment);
        Experiment timeoutExperiment =
                TestHelperUtils.createSentExperiment(UUID.randomUUID().toString(), RequestStatus.TIMEOUT,
                        LocalDateTime.now());
        timeoutExperiment.setAppInstanceEntity(appInstanceEntity);
        timeoutExperiment.setDeletedDate(LocalDateTime.now());
        experiments.add(timeoutExperiment);
        experimentRepository.saveAll(experiments);
        experimentScheduler.processRequestsToRemove();
        verify(experimentService).removeExperimentData(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(experimentToRemove);
    }

    @Test
    void testSentExperimentsToErs() {
        //Create finished experiment
        Experiment finishedExperiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED,
                        appInstanceEntity);
        experimentRepository.save(finishedExperiment);
        ExperimentResultsEntity firstResults = TestHelperUtils.createExperimentResultsEntity(finishedExperiment);
        ExperimentResultsEntity secondResults = TestHelperUtils.createExperimentResultsEntity(finishedExperiment);
        ExperimentResultsEntity thirdResults = TestHelperUtils.createExperimentResultsEntity(finishedExperiment);
        experimentResultsEntityRepository.saveAll(Arrays.asList(firstResults, secondResults, thirdResults));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(firstResults, ErsResponseStatus.ERROR));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(firstResults, ErsResponseStatus.DUPLICATE_REQUEST_ID));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(secondResults, ErsResponseStatus.SUCCESS));
        //Created deleted experiment
        Experiment removedExperiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED,
                        appInstanceEntity);
        removedExperiment.setDeletedDate(LocalDateTime.now());
        experimentRepository.save(removedExperiment);
        //Create error experiment
        Experiment errorExperiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.TIMEOUT,
                        appInstanceEntity);
        experimentRepository.save(errorExperiment);
        //Create another finished experiment
        finishedExperiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED,
                appInstanceEntity);
        experimentRepository.save(finishedExperiment);
        experimentResultsEntityRepository.save(TestHelperUtils.createExperimentResultsEntity(finishedExperiment));
        when(experimentService.getExperimentHistory(any(Experiment.class))).thenReturn(new ExperimentHistory());
        experimentScheduler.processRequestsToErs();
        verify(ersService, times(3)).sentExperimentResults(any(ExperimentResultsEntity.class),
                any(ExperimentHistory.class), any(ExperimentResultsRequestSource.class));
    }
}
