package com.ecaservice.service.scheduler;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.exception.ExperimentException;
import com.ecaservice.model.entity.ErsRequest;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsRequest;
import com.ecaservice.model.experiment.ExperimentResultsRequestStatus;
import com.ecaservice.model.experiment.ExperimentStatus;
import com.ecaservice.repository.ErsRequestRepository;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.repository.ExperimentResultsRequestRepository;
import com.ecaservice.service.EvaluationResultsService;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.service.experiment.mail.NotificationService;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationResults;
import eca.trees.C45;
import eca.trees.CART;
import org.junit.After;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    @Inject
    private ExperimentResultsRequestRepository experimentResultsRequestRepository;
    @Inject
    private ErsRequestRepository ersRequestRepository;
    @Mock
    private ExperimentService experimentService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private EvaluationResultsService evaluationResultsService;
    @Captor
    private ArgumentCaptor<Experiment> argumentCaptor;

    @Inject
    private ExperimentConfig experimentConfig;

    private ExperimentScheduler experimentScheduler;

    private Evaluation evaluation;

    @Before
    public void setUp() throws Exception {
        experimentScheduler =
                new ExperimentScheduler(experimentRepository, experimentResultsRequestRepository, experimentService,
                        notificationService, evaluationResultsService, experimentConfig);
        evaluation = new Evaluation(TestHelperUtils.loadInstances());
    }

    @After
    public void after() {
        ersRequestRepository.deleteAll();
        experimentResultsRequestRepository.deleteAll();
        experimentRepository.deleteAll();
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

    @Test
    public void testSuccessProcessingExperimentResultsRequests() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experimentRepository.save(experiment);
        ExperimentResultsRequest resultsRequest = TestHelperUtils.createExperimentResultsRequest(experiment);
        experimentResultsRequestRepository.save(resultsRequest);
        List<EvaluationResults> evaluationResults =
                Arrays.asList(new EvaluationResults(new CART(), evaluation), new EvaluationResults(new C45(), evaluation));
        when(experimentService.getEvaluationResults(experiment)).thenReturn(evaluationResults);
        experimentScheduler.processExperimentResultsRequests();
        verify(evaluationResultsService, times(evaluationResults.size())).saveEvaluationResults(any(EvaluationResults
                .class), any(ErsRequest.class));
        ExperimentResultsRequest actual = experimentResultsRequestRepository.findOne(resultsRequest.getId());
        assertThat(actual.getSentDate()).isNotNull();
        assertThat(actual.getRequestStatus()).isEqualTo(ExperimentResultsRequestStatus.COMPLETE);
    }

    @Test
    public void testErrorProcessingExperimentResultsRequests() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experimentRepository.save(experiment);
        ExperimentResultsRequest resultsRequest = TestHelperUtils.createExperimentResultsRequest(experiment);
        experimentResultsRequestRepository.save(resultsRequest);
        when(experimentService.getEvaluationResults(experiment)).thenThrow(new ExperimentException("Error"));
        experimentScheduler.processExperimentResultsRequests();
        verify(evaluationResultsService, never()).saveEvaluationResults(any(EvaluationResults
                .class), any(ErsRequest.class));
        ExperimentResultsRequest actual = experimentResultsRequestRepository.findOne(resultsRequest.getId());
        assertThat(actual.getSentDate()).isNotNull();
        assertThat(actual.getRequestStatus()).isEqualTo(ExperimentResultsRequestStatus.ERROR);
    }
}
