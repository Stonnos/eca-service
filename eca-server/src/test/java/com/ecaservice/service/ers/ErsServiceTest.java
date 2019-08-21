package com.ecaservice.service.ers;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.dto.evaluation.GetEvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.mapping.ClassificationCostsMapperImpl;
import com.ecaservice.mapping.ClassifierInfoMapperImpl;
import com.ecaservice.mapping.EvaluationLogDetailsMapper;
import com.ecaservice.mapping.EvaluationLogDetailsMapperImpl;
import com.ecaservice.mapping.ClassifierInputOptionsMapperImpl;
import com.ecaservice.mapping.InstancesInfoMapperImpl;
import com.ecaservice.mapping.StatisticsReportMapperImpl;
import com.ecaservice.model.entity.ErsRequest;
import com.ecaservice.model.entity.ErsResponseStatus;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.repository.EvaluationResultsRequestEntityRepository;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.repository.ExperimentResultsRequestRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.web.dto.model.ErsReportDto;
import com.ecaservice.web.dto.model.ErsReportStatus;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import eca.converters.model.ExperimentHistory;
import eca.core.evaluation.EvaluationResults;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.ws.client.WebServiceIOException;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ErsService} functionality.
 *
 * @author Roman Batygin
 */
@Import({ExperimentConfig.class, EvaluationLogDetailsMapperImpl.class, InstancesInfoMapperImpl.class,
        ClassifierInputOptionsMapperImpl.class, StatisticsReportMapperImpl.class,
        ClassificationCostsMapperImpl.class, ClassifierInfoMapperImpl.class})
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
    @Inject
    private EvaluationLogRepository evaluationLogRepository;

    private ErsService ersService;

    @Override
    public void init() {
        ersService = new ErsService(ersRequestService, experimentConfig, evaluationLogDetailsMapper,
                experimentResultsRequestRepository,
                evaluationResultsRequestEntityRepository);
    }

    @Override
    public void deleteAll() {
        experimentResultsRequestRepository.deleteAll();
        experimentRepository.deleteAll();
        evaluationResultsRequestEntityRepository.deleteAll();
        evaluationLogRepository.deleteAll();
    }

    @Test
    public void testErsReportWithExperimentInProgressStatus() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experimentRepository.save(experiment);
        ErsReportDto ersReportDto = ersService.getErsReport(experiment);
        Assertions.assertThat(ersReportDto).isNotNull();
        Assertions.assertThat(ersReportDto.getErsReportStatus().getValue()).isEqualTo(
                ErsReportStatus.EXPERIMENT_IN_PROGRESS.name());
    }

    @Test
    public void testErsReportWithExperimentErrorStatus() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR);
        experimentRepository.save(experiment);
        ErsReportDto ersReportDto = ersService.getErsReport(experiment);
        Assertions.assertThat(ersReportDto).isNotNull();
        Assertions.assertThat(ersReportDto.getErsReportStatus().getValue()).isEqualTo(
                ErsReportStatus.EXPERIMENT_ERROR.name());
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
        Assertions.assertThat(ersReportDto.getErsReportStatus().getValue()).isEqualTo(
                ErsReportStatus.EXPERIMENT_DELETED.name());
        experimentRepository.deleteAll();
        //Case 2
        experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experiment.setDeletedDate(LocalDateTime.now());
        experimentRepository.save(experiment);
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experiment, ErsResponseStatus.SUCCESS));
        ersReportDto = ersService.getErsReport(experiment);
        Assertions.assertThat(ersReportDto).isNotNull();
        Assertions.assertThat(ersReportDto.getErsReportStatus().getValue()).isEqualTo(
                ErsReportStatus.SUCCESS_SENT.name());
    }

    @Test
    public void testErsReportWithNeedSentStatus() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experimentRepository.save(experiment);
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experiment, ErsResponseStatus.ERROR));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experiment, ErsResponseStatus.INVALID_REQUEST_PARAMS));
        ErsReportDto ersReportDto = ersService.getErsReport(experiment);
        Assertions.assertThat(ersReportDto).isNotNull();
        Assertions.assertThat(ersReportDto.getErsReportStatus().getValue()).isEqualTo(ErsReportStatus.NEED_SENT.name());
    }

    @Test
    public void testErsReportBase() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experimentRepository.save(experiment);
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experiment, ErsResponseStatus.ERROR));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experiment, ErsResponseStatus.INVALID_REQUEST_PARAMS));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experiment, ErsResponseStatus.DUPLICATE_REQUEST_ID));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experiment, ErsResponseStatus.SUCCESS));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experiment, ErsResponseStatus.SUCCESS));
        ErsReportDto ersReportDto = ersService.getErsReport(experiment);
        Assertions.assertThat(ersReportDto).isNotNull();
        Assertions.assertThat(ersReportDto.getErsReportStatus().getValue()).isEqualTo(
                ErsReportStatus.SUCCESS_SENT.name());
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

    @Test
    public void testGetEvaluationLogDetailsWithInProgressStatus() {
        EvaluationLog evaluationLog =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.NEW);
        EvaluationLogDetailsDto evaluationLogDetailsDto = ersService.getEvaluationLogDetails(evaluationLog);
        Assertions.assertThat(evaluationLogDetailsDto).isNotNull();
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsDto()).isNull();
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsStatus().getValue()).isEqualTo(
                EvaluationResultsStatus.EVALUATION_IN_PROGRESS.name());
    }

    @Test
    public void testGetEvaluationLogDetailsWithEvaluationErrorStatus() {
        EvaluationLog evaluationLog =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.ERROR);
        EvaluationLogDetailsDto evaluationLogDetailsDto = ersService.getEvaluationLogDetails(evaluationLog);
        Assertions.assertThat(evaluationLogDetailsDto).isNotNull();
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsDto()).isNull();
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsStatus().getValue()).isEqualTo(
                EvaluationResultsStatus.EVALUATION_ERROR.name());
    }

    /**
     * Case 1: There is no one request to ERS
     * Case 2: There is no one ERS request with status SUCCESS
     */
    @Test
    public void testGetEvaluationLogDetailsWithNotSentStatus() {
        //Case 1
        EvaluationLog evaluationLog =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        evaluationLogRepository.save(evaluationLog);
        EvaluationLogDetailsDto evaluationLogDetailsDto = ersService.getEvaluationLogDetails(evaluationLog);
        Assertions.assertThat(evaluationLogDetailsDto).isNotNull();
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsDto()).isNull();
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsStatus().getValue()).isEqualTo(
                EvaluationResultsStatus.RESULTS_NOT_SENT.name());
        //Case 2
        EvaluationResultsRequestEntity evaluationResultsRequestEntity = new EvaluationResultsRequestEntity();
        evaluationResultsRequestEntity.setRequestDate(LocalDateTime.now().minusDays(1L));
        evaluationResultsRequestEntity.setRequestId(UUID.randomUUID().toString());
        evaluationResultsRequestEntity.setResponseStatus(ErsResponseStatus.ERROR);
        evaluationResultsRequestEntity.setEvaluationLog(evaluationLog);
        evaluationResultsRequestEntityRepository.save(evaluationResultsRequestEntity);
        evaluationLogDetailsDto = ersService.getEvaluationLogDetails(evaluationLog);
        Assertions.assertThat(evaluationLogDetailsDto).isNotNull();
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsDto()).isNull();
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsStatus().getValue()).isEqualTo(
                EvaluationResultsStatus.RESULTS_NOT_SENT.name());
    }

    @Test
    public void testGetEvaluationResultsNotFound() {
        EvaluationLog evaluationLog = createAndSaveFinishedEvaluationLog();
        GetEvaluationResultsResponse response = new GetEvaluationResultsResponse();
        response.setStatus(ResponseStatus.RESULTS_NOT_FOUND);
        when(ersRequestService.getEvaluationResults(anyString())).thenReturn(response);
        EvaluationLogDetailsDto evaluationLogDetailsDto = ersService.getEvaluationLogDetails(evaluationLog);
        Assertions.assertThat(evaluationLogDetailsDto).isNotNull();
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsDto()).isNull();
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsStatus().getValue()).isEqualTo(
                EvaluationResultsStatus.EVALUATION_RESULTS_NOT_FOUND.name());
    }

    @Test
    public void testGetEvaluationResultsWithResponseErrorStatus() {
        EvaluationLog evaluationLog = createAndSaveFinishedEvaluationLog();
        GetEvaluationResultsResponse response = new GetEvaluationResultsResponse();
        response.setStatus(ResponseStatus.ERROR);
        when(ersRequestService.getEvaluationResults(anyString())).thenReturn(response);
        EvaluationLogDetailsDto evaluationLogDetailsDto = ersService.getEvaluationLogDetails(evaluationLog);
        Assertions.assertThat(evaluationLogDetailsDto).isNotNull();
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsDto()).isNull();
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsStatus().getValue()).isEqualTo(
                EvaluationResultsStatus.ERROR.name());
    }

    @Test
    public void testGetEvaluationResultsWithServiceUnavailable() {
        EvaluationLog evaluationLog = createAndSaveFinishedEvaluationLog();
        when(ersRequestService.getEvaluationResults(anyString())).thenThrow(new WebServiceIOException("I/O"));
        EvaluationLogDetailsDto evaluationLogDetailsDto = ersService.getEvaluationLogDetails(evaluationLog);
        Assertions.assertThat(evaluationLogDetailsDto).isNotNull();
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsDto()).isNull();
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsStatus().getValue()).isEqualTo(
                EvaluationResultsStatus.ERS_SERVICE_UNAVAILABLE.name());
    }

    @Test
    public void testGetEvaluationResultsWithUnknownError() {
        EvaluationLog evaluationLog = createAndSaveFinishedEvaluationLog();
        when(ersRequestService.getEvaluationResults(anyString())).thenThrow(new RuntimeException());
        EvaluationLogDetailsDto evaluationLogDetailsDto = ersService.getEvaluationLogDetails(evaluationLog);
        Assertions.assertThat(evaluationLogDetailsDto).isNotNull();
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsDto()).isNull();
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsStatus().getValue()).isEqualTo(
                EvaluationResultsStatus.ERROR.name());
    }

    @Test
    public void testSuccessGetEvaluationLogDetails() {
        EvaluationLog evaluationLog = createAndSaveFinishedEvaluationLog();
        GetEvaluationResultsResponse response = new GetEvaluationResultsResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        response.setStatistics(TestHelperUtils.createStatisticsReport());
        when(ersRequestService.getEvaluationResults(anyString())).thenReturn(response);
        EvaluationLogDetailsDto evaluationLogDetailsDto = ersService.getEvaluationLogDetails(evaluationLog);
        Assertions.assertThat(evaluationLogDetailsDto).isNotNull();
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsDto()).isNotNull();
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsStatus().getValue()).isEqualTo(
                EvaluationResultsStatus.RESULTS_RECEIVED.name());
    }

    private EvaluationLog createAndSaveFinishedEvaluationLog() {
        EvaluationLog evaluationLog =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        EvaluationResultsRequestEntity evaluationResultsRequestEntity = new EvaluationResultsRequestEntity();
        evaluationResultsRequestEntity.setRequestDate(LocalDateTime.now().minusDays(1L));
        evaluationResultsRequestEntity.setRequestId(UUID.randomUUID().toString());
        evaluationResultsRequestEntity.setResponseStatus(ErsResponseStatus.SUCCESS);
        evaluationResultsRequestEntity.setEvaluationLog(evaluationLog);
        evaluationLogRepository.save(evaluationLog);
        evaluationResultsRequestEntityRepository.save(evaluationResultsRequestEntity);
        return evaluationLog;
    }
}
