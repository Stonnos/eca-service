package com.ecaservice.service.scheduler;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentStatus;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.experiment.AbstractExperimentTest;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.service.experiment.mail.NotificationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests that checks ExperimentScheduler functionality (see {@link ExperimentScheduler}).
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(ExperimentConfig.class)
public class ExperimentSchedulerTest extends AbstractExperimentTest {

    @Autowired
    private ExperimentRepository experimentRepository;
    @Mock
    private ExperimentService experimentService;
    @Mock
    private NotificationService notificationService;
    @Autowired
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
        experimentScheduler.processingNewRequests();
        verify(experimentService, times(experiments.size())).processExperiment(any(Experiment.class));
    }

    @Test
    public void testSentExperiments() {
        List<Experiment> experiments = new ArrayList<>();
        experiments.add(TestHelperUtils.createExperiment(UUID.randomUUID().toString(), ExperimentStatus.FINISHED));
        experiments.add(TestHelperUtils.createExperiment(UUID.randomUUID().toString(), ExperimentStatus.ERROR));
        experiments.add(TestHelperUtils.createExperiment(UUID.randomUUID().toString(), ExperimentStatus.TIMEOUT));
        experimentRepository.save(experiments);
        experimentScheduler.processingRequestsToSent();
        verify(notificationService, times(experiments.size())).notifyByEmail(any(Experiment.class));
    }

    @Test
    public void testRemoveExperiments() {
        List<Experiment> experiments = new ArrayList<>();
        experiments.add(TestHelperUtils.createExperiment(UUID.randomUUID().toString(), ExperimentStatus.FINISHED));
        experiments.add(TestHelperUtils.createSentExperiment(UUID.randomUUID().toString(), ExperimentStatus.ERROR,
                LocalDateTime.now().minusDays(experimentConfig.getNumberOfDaysForStorage() + 1)));
        experiments.add(TestHelperUtils.createSentExperiment(UUID.randomUUID().toString(), ExperimentStatus.FINISHED,
                LocalDateTime.now()));
        Experiment experiment =
                TestHelperUtils.createSentExperiment(UUID.randomUUID().toString(), ExperimentStatus.TIMEOUT,
                        LocalDateTime.now());
        experiment.setDeletedDate(LocalDateTime.now());
        experiments.add(experiment);
        experimentRepository.save(experiments);
        experimentScheduler.processingRequestsToRemove();
        verify(experimentService, times(1)).removeExperimentData(any(Experiment.class));
    }
}
