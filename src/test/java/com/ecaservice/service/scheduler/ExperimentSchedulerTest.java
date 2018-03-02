package com.ecaservice.service.scheduler;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentStatus;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.service.experiment.mail.NotificationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests that checks ExperimentScheduler functionality {@see ExperimentScheduler}.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ExperimentSchedulerTest {

    @Inject
    private ExperimentRepository experimentRepository;
    @Mock
    private ExperimentService experimentService;
    @Mock
    private NotificationService notificationService;
    @Captor
    private ArgumentCaptor<Experiment> argumentCaptor;

    @Inject
    private ExperimentConfig experimentConfig;

    private ExperimentScheduler experimentScheduler;

    @Before
    public void setUp() {
        experimentRepository.deleteAll();
        experimentScheduler = new ExperimentScheduler(experimentRepository, experimentService, notificationService,
                experimentConfig);
    }

    @Test
    public void testProcessExperiments() {
        List<Experiment> experiments = new ArrayList<>();
        experiments.add(TestHelperUtils.createExperiment(UUID.randomUUID().toString()));
        experiments.add(TestHelperUtils.createExperiment(UUID.randomUUID().toString()));
        experimentRepository.save(experiments);
        experimentScheduler.processNewRequests();
        verify(experimentService, times(experiments.size())).processExperiment(any(Experiment.class));
    }

    @Test
    public void testSentExperiments() {
        List<Experiment> experiments = new ArrayList<>();
        experiments.add(TestHelperUtils.createExperiment(UUID.randomUUID().toString(), ExperimentStatus.FINISHED));
        experiments.add(TestHelperUtils.createExperiment(UUID.randomUUID().toString(), ExperimentStatus.ERROR));
        experiments.add(TestHelperUtils.createExperiment(UUID.randomUUID().toString(), ExperimentStatus.TIMEOUT));
        experimentRepository.save(experiments);
        experimentScheduler.processRequestsToSent();
        verify(notificationService, times(experiments.size())).notifyByEmail(any(Experiment.class));
    }

    @Test
    public void testRemoveExperiments() {
        List<Experiment> experiments = new ArrayList<>();
        experiments.add(TestHelperUtils.createExperiment(UUID.randomUUID().toString(), ExperimentStatus.FINISHED));
        Experiment experimentToRemove =
                TestHelperUtils.createSentExperiment(UUID.randomUUID().toString(), ExperimentStatus.ERROR,
                        LocalDateTime.now().minusDays(experimentConfig.getNumberOfDaysForStorage() + 1));
        experiments.add(experimentToRemove);
        experiments.add(TestHelperUtils.createSentExperiment(UUID.randomUUID().toString(), ExperimentStatus.FINISHED,
                LocalDateTime.now()));
        Experiment experiment =
                TestHelperUtils.createSentExperiment(UUID.randomUUID().toString(), ExperimentStatus.TIMEOUT,
                        LocalDateTime.now());
        experiment.setDeletedDate(LocalDateTime.now());
        experiments.add(experiment);
        experimentRepository.save(experiments);
        experimentScheduler.processRequestsToRemove();
        verify(experimentService).removeExperimentData(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(experimentToRemove);
    }
}
