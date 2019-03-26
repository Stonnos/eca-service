package com.ecaservice.service.ers;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.mapping.EvaluationLogDetailsMapper;
import com.ecaservice.mapping.EvaluationLogDetailsMapperImpl;
import com.ecaservice.mapping.EvaluationLogInputOptionsMapperImpl;
import com.ecaservice.mapping.InstancesInfoMapperImpl;
import com.ecaservice.mapping.StatisticsReportMapperImpl;
import com.ecaservice.model.entity.ErsRequest;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.repository.EvaluationResultsRequestEntityRepository;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.repository.ExperimentResultsRequestRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.web.dto.model.ErsReportDto;
import com.ecaservice.web.dto.model.ErsReportStatus;
import eca.converters.model.ExperimentHistory;
import eca.core.evaluation.EvaluationResults;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for checking {@link ErsService} functionality.
 *
 * @author Roman Batygin
 */
@Import({ExperimentConfig.class, EvaluationLogDetailsMapperImpl.class, InstancesInfoMapperImpl.class,
        EvaluationLogInputOptionsMapperImpl.class, StatisticsReportMapperImpl.class})
public class ErsServiceTest extends AbstractJpaTest {

    @Mock
    private ErsRequestService ersRequestService;
    @Inject
    private ExperimentConfig experimentConfig;
    @Inject
    private ExperimentResultsRequestRepository experimentResultsRequestRepository;
    @Inject
    private EvaluationResultsRequestEntityRepository evaluationResultsRequestEntityRepository;
    @Inject
    private EvaluationLogDetailsMapper evaluationLogDetailsMapper;
    @Inject
    private ExperimentRepository experimentRepository;

    private ErsService ersService;

    @Before
    public void init() {
        experimentResultsRequestRepository.deleteAll();
        experimentRepository.deleteAll();
        ersService = new ErsService(ersRequestService, experimentConfig, evaluationLogDetailsMapper,
                experimentResultsRequestRepository,
                evaluationResultsRequestEntityRepository);
    }

    @Test
    public void testErsReportWithExperimentInProgressStatus() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experimentRepository.save(experiment);
        ErsReportDto ersReportDto = ersService.getErsReport(experiment);
        Assertions.assertThat(ersReportDto).isNotNull();
        Assertions.assertThat(ersReportDto.getErsReportStatus()).isEqualTo(ErsReportStatus.EXPERIMENT_IN_PROGRESS);
    }

    @Test
    public void testErsReportWithExperimentErrorStatus() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR);
        experimentRepository.save(experiment);
        ErsReportDto ersReportDto = ersService.getErsReport(experiment);
        Assertions.assertThat(ersReportDto).isNotNull();
        Assertions.assertThat(ersReportDto.getErsReportStatus()).isEqualTo(ErsReportStatus.EXPERIMENT_ERROR);
    }

    /**
     * Case 1: There is no one success requests to ERS service and experiment is deleted. Expected: EXPERIMENT_DELETED
     * Case 2: There is no one success requests to ERS service and experiment is deleted. Expected: SUCCESS_SENT
     */
    @Test
    public void testErsReportWithExperimentDeletedStatus() {
        //Case 1
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experiment.setDeletedDate(LocalDateTime.now());
        experimentRepository.save(experiment);
        ErsReportDto ersReportDto = ersService.getErsReport(experiment);
        Assertions.assertThat(ersReportDto).isNotNull();
        Assertions.assertThat(ersReportDto.getErsReportStatus()).isEqualTo(ErsReportStatus.EXPERIMENT_DELETED);
        experimentRepository.deleteAll();
        //Case 2
        experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experiment.setDeletedDate(LocalDateTime.now());
        experimentRepository.save(experiment);
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experiment, ResponseStatus.SUCCESS));
        ersReportDto = ersService.getErsReport(experiment);
        Assertions.assertThat(ersReportDto).isNotNull();
        Assertions.assertThat(ersReportDto.getErsReportStatus()).isEqualTo(ErsReportStatus.SUCCESS_SENT);
    }

    @Test
    public void testErsReportWithNeedSentStatus() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experimentRepository.save(experiment);
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experiment, ResponseStatus.ERROR));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experiment, ResponseStatus.INVALID_REQUEST_PARAMS));
        ErsReportDto ersReportDto = ersService.getErsReport(experiment);
        Assertions.assertThat(ersReportDto).isNotNull();
        Assertions.assertThat(ersReportDto.getErsReportStatus()).isEqualTo(ErsReportStatus.NEED_SENT);
    }

    @Test
    public void testErsReportBase() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experimentRepository.save(experiment);
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experiment, ResponseStatus.ERROR));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experiment, ResponseStatus.INVALID_REQUEST_PARAMS));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experiment, ResponseStatus.DUPLICATE_REQUEST_ID));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experiment, ResponseStatus.SUCCESS));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experiment, ResponseStatus.SUCCESS));
        ErsReportDto ersReportDto = ersService.getErsReport(experiment);
        Assertions.assertThat(ersReportDto).isNotNull();
        Assertions.assertThat(ersReportDto.getErsReportStatus()).isEqualTo(ErsReportStatus.SUCCESS_SENT);
        Assertions.assertThat(ersReportDto.getRequestsCount()).isEqualTo(5);
        Assertions.assertThat(ersReportDto.getSuccessfullySavedClassifiers()).isEqualTo(2);
        Assertions.assertThat(ersReportDto.getFailedRequestsCount()).isEqualTo(3);
    }

    @Test
    public void testSentExperimentHistory() throws Exception {
        ExperimentHistory experimentHistory = new ExperimentHistory();
        experimentHistory.setExperiment(new ArrayList<>());
        experimentHistory.getExperiment().add(TestHelperUtils.getEvaluationResults());
        experimentHistory.getExperiment().add(TestHelperUtils.getEvaluationResults());
        doNothing().when(ersRequestService).saveEvaluationResults(any(EvaluationResults.class), any(ErsRequest.class));
        ersService.sentExperimentHistory(new Experiment(), experimentHistory, ExperimentResultsRequestSource.MANUAL);
        verify(ersRequestService, times(experimentHistory.getExperiment().size())).saveEvaluationResults(
                any(EvaluationResults.class), any(ErsRequest.class));
    }
}
