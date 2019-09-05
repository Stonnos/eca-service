package com.ecaservice.service.scheduler;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.mapping.ClassifierInfoMapperImpl;
import com.ecaservice.mapping.ClassifierInputOptionsMapperImpl;
import com.ecaservice.mapping.ExperimentMapperImpl;
import com.ecaservice.mapping.ExperimentResultsMapper;
import com.ecaservice.mapping.ExperimentResultsMapperImpl;
import com.ecaservice.model.entity.ErsResponseStatus;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.repository.EmailRequestRepository;
import com.ecaservice.repository.ErsRequestRepository;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.repository.ExperimentResultsEntityRepository;
import com.ecaservice.repository.ExperimentResultsRequestRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.ers.ErsService;
import com.ecaservice.service.experiment.ExperimentResultsService;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests that checks ExperimentScheduler functionality {@see ExperimentScheduler}.
 *
 * @author Roman Batygin
 */
@Import({ExperimentConfig.class, ExperimentResultsMapperImpl.class,
        ClassifierInfoMapperImpl.class, ClassifierInputOptionsMapperImpl.class, ExperimentMapperImpl.class})
public class ExperimentSchedulerTest extends AbstractJpaTest {

    @Inject
    private ExperimentResultsMapper experimentResultsMapper;
    @Inject
    private ExperimentRepository experimentRepository;
    @Inject
    private ErsRequestRepository ersRequestRepository;
    @Inject
    private EmailRequestRepository emailRequestRepository;
    @Inject
    private ExperimentResultsRequestRepository experimentResultsRequestRepository;
    @Inject
    private ExperimentResultsEntityRepository experimentResultsEntityRepository;
    @Mock
    private ExperimentService experimentService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private ErsService ersService;
    @Mock
    private ExperimentResultsService experimentResultsService;
    @Captor
    private ArgumentCaptor<Experiment> argumentCaptor;

    @Inject
    private ExperimentConfig experimentConfig;

    private ExperimentScheduler experimentScheduler;

    @Override
    public void init() {
        experimentScheduler =
                new ExperimentScheduler(experimentRepository, experimentResultsEntityRepository, experimentService,
                        notificationService, ersService, experimentConfig, experimentResultsMapper,
                        experimentResultsService);
    }

    @Override
    public void deleteAll() {
        ersRequestRepository.deleteAll();
        emailRequestRepository.deleteAll();
        experimentResultsEntityRepository.deleteAll();
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
        //Create finished experiment
        Experiment finishedExperiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experimentRepository.save(finishedExperiment);
        ExperimentResultsEntity firstResults = TestHelperUtils.createExperimentResultsEntity(finishedExperiment);
        ExperimentResultsEntity secondResults = TestHelperUtils.createExperimentResultsEntity(finishedExperiment);
        ExperimentResultsEntity thirdResults = TestHelperUtils.createExperimentResultsEntity(finishedExperiment);
        experimentResultsEntityRepository.saveAll(Arrays.asList(firstResults, secondResults, thirdResults));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(firstResults, finishedExperiment,
                        ErsResponseStatus.ERROR));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(firstResults, finishedExperiment,
                        ErsResponseStatus.DUPLICATE_REQUEST_ID));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(secondResults, finishedExperiment,
                        ErsResponseStatus.SUCCESS));
        //Created deleted experiment
        Experiment removedExperiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        removedExperiment.setDeletedDate(LocalDateTime.now());
        experimentRepository.save(removedExperiment);
        //Create error experiment
        Experiment errorExperiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.TIMEOUT);
        experimentRepository.save(errorExperiment);
        //Create another finished experiment
        finishedExperiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experimentRepository.save(finishedExperiment);
        experimentResultsEntityRepository.save(TestHelperUtils.createExperimentResultsEntity(finishedExperiment));
        when(experimentService.getExperimentHistory(any(Experiment.class))).thenReturn(new ExperimentHistory());
        experimentScheduler.processRequestsToErs();
        verify(ersService, times(3)).sentExperimentResults(any(ExperimentResultsEntity.class),
                any(ExperimentHistory.class), any(ExperimentResultsRequestSource.class));
    }
}
