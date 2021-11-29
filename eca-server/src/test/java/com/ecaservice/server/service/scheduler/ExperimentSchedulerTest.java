package com.ecaservice.server.service.scheduler;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.event.model.ExperimentEmailEvent;
import com.ecaservice.server.event.model.ExperimentWebPushEvent;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.experiment.ExperimentProgressService;
import com.ecaservice.server.service.experiment.ExperimentRequestProcessor;
import com.ecaservice.server.service.experiment.ExperimentService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests that checks ExperimentScheduler functionality {@see ExperimentScheduler}.
 *
 * @author Roman Batygin
 */
@Import({ExperimentConfig.class, AppProperties.class})
class ExperimentSchedulerTest extends AbstractJpaTest {

    private static final int EXPECTED_CHANGE_STATUS_EVENTS_COUNT = 2;

    @Inject
    private ExperimentRepository experimentRepository;
    @Mock
    private ExperimentService experimentService;
    @Mock
    private ExperimentProgressService experimentProgressService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Captor
    private ArgumentCaptor<Experiment> argumentCaptor;

    @Inject
    private ExperimentConfig experimentConfig;

    private ExperimentScheduler experimentScheduler;

    @Override
    public void init() {
        ExperimentRequestProcessor experimentRequestProcessor =
                new ExperimentRequestProcessor(experimentRepository, experimentService, eventPublisher,
                        experimentProgressService, experimentConfig);
        experimentScheduler = new ExperimentScheduler(experimentRequestProcessor, experimentRepository);
    }

    @Override
    public void deleteAll() {
        experimentRepository.deleteAll();
    }

    @Test
    void testProcessExperiments() {
        List<Experiment> experiments = newArrayList();
        experiments.add(
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW));
        experiments.add(
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR));
        experiments.add(
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED));
        experimentRepository.saveAll(experiments);
        var iterator = experiments.iterator();
        when(experimentService.getById(anyLong())).thenReturn(iterator.next());
        experimentScheduler.processNewRequests();
        verify(experimentService, atLeastOnce()).processExperiment(any(Experiment.class));
        verify(eventPublisher, times(EXPECTED_CHANGE_STATUS_EVENTS_COUNT)).publishEvent(
                any(ExperimentEmailEvent.class));
        verify(eventPublisher, times(EXPECTED_CHANGE_STATUS_EVENTS_COUNT)).publishEvent(
                any(ExperimentWebPushEvent.class));
    }

    @Test
    void testRemoveExperiments() {
        List<Experiment> experiments = newArrayList();
        experiments.add(TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED));
        Experiment experimentToRemove =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experimentToRemove.setEndDate(LocalDateTime.now().minusDays(experimentConfig.getNumberOfDaysForStorage() + 1));
        experiments.add(experimentToRemove);
        Experiment finishedExperiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experiments.add(finishedExperiment);
        Experiment timeoutExperiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.TIMEOUT);
        timeoutExperiment.setDeletedDate(LocalDateTime.now());
        experiments.add(timeoutExperiment);
        Experiment errorExperiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.TIMEOUT);
        experiments.add(errorExperiment);
        experimentRepository.saveAll(experiments);
        experimentScheduler.processRequestsToRemove();
        verify(experimentService).removeExperimentModel(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(experimentToRemove);
    }
}
