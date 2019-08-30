package com.ecaservice.service.ers;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.evaluation.GetEvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.mapping.ClassificationCostsMapperImpl;
import com.ecaservice.mapping.ClassifierInfoMapperImpl;
import com.ecaservice.mapping.ClassifierInputOptionsMapperImpl;
import com.ecaservice.mapping.EvaluationLogDetailsMapper;
import com.ecaservice.mapping.EvaluationLogDetailsMapperImpl;
import com.ecaservice.mapping.ExperimentResultsDetailsMapper;
import com.ecaservice.mapping.ExperimentResultsDetailsMapperImpl;
import com.ecaservice.mapping.ExperimentResultsMapper;
import com.ecaservice.mapping.ExperimentResultsMapperImpl;
import com.ecaservice.mapping.GetEvaluationResultsMapper;
import com.ecaservice.mapping.GetEvaluationResultsMapperImpl;
import com.ecaservice.mapping.InstancesInfoMapperImpl;
import com.ecaservice.mapping.StatisticsReportMapperImpl;
import com.ecaservice.model.entity.ErsRequest;
import com.ecaservice.model.entity.ErsResponseStatus;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.entity.ExperimentResultsRequest;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.repository.EvaluationResultsRequestEntityRepository;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.repository.ExperimentResultsEntityRepository;
import com.ecaservice.repository.ExperimentResultsRequestRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.web.dto.model.ErsReportStatus;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import com.ecaservice.web.dto.model.ExperimentErsReportDto;
import com.ecaservice.web.dto.model.ExperimentResultsDetailsDto;
import eca.converters.model.ExperimentHistory;
import eca.core.evaluation.EvaluationResults;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.ws.client.WebServiceIOException;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
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
@Import({EvaluationLogDetailsMapperImpl.class, InstancesInfoMapperImpl.class,
        ClassifierInputOptionsMapperImpl.class, StatisticsReportMapperImpl.class,
        ClassificationCostsMapperImpl.class, ExperimentResultsMapperImpl.class,
        ExperimentResultsDetailsMapperImpl.class, ClassifierInfoMapperImpl.class, GetEvaluationResultsMapperImpl.class})
public class ErsServiceTest extends AbstractJpaTest {

    @Mock
    private ErsRequestService ersRequestService;
    @Inject
    private ExperimentResultsRequestRepository experimentResultsRequestRepository;
    @Inject
    private EvaluationResultsRequestEntityRepository evaluationResultsRequestEntityRepository;
    @Inject
    private EvaluationLogDetailsMapper evaluationLogDetailsMapper;
    @Inject
    private ExperimentResultsMapper experimentResultsMapper;
    @Inject
    private ExperimentResultsDetailsMapper experimentResultsDetailsMapper;
    @Inject
    private GetEvaluationResultsMapper evaluationResultsMapper;
    @Inject
    private ExperimentRepository experimentRepository;
    @Inject
    private EvaluationLogRepository evaluationLogRepository;
    @Inject
    private ExperimentResultsEntityRepository experimentResultsEntityRepository;

    private ErsService ersService;

    @Override
    public void init() {
        ersService = new ErsService(ersRequestService, evaluationLogDetailsMapper, experimentResultsDetailsMapper,
                experimentResultsMapper, evaluationResultsMapper, evaluationResultsRequestEntityRepository,
                experimentResultsEntityRepository,
                experimentResultsRequestRepository);
    }

    @Override
    public void deleteAll() {
        experimentResultsRequestRepository.deleteAll();
        experimentResultsEntityRepository.deleteAll();
        experimentRepository.deleteAll();
        evaluationResultsRequestEntityRepository.deleteAll();
        evaluationLogRepository.deleteAll();
    }

    @Test
    public void testErsReportWithExperimentInProgressStatus() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experimentRepository.save(experiment);
        ExperimentErsReportDto experimentErsReportDto = ersService.getErsReport(experiment);
        Assertions.assertThat(experimentErsReportDto).isNotNull();
        Assertions.assertThat(experimentErsReportDto.getErsReportStatus().getValue()).isEqualTo(
                ErsReportStatus.EXPERIMENT_IN_PROGRESS.name());
    }

    @Test
    public void testErsReportWithExperimentErrorStatus() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR);
        experimentRepository.save(experiment);
        ExperimentErsReportDto experimentErsReportDto = ersService.getErsReport(experiment);
        Assertions.assertThat(experimentErsReportDto).isNotNull();
        Assertions.assertThat(experimentErsReportDto.getErsReportStatus().getValue()).isEqualTo(
                ErsReportStatus.EXPERIMENT_ERROR.name());
    }

    /**
     * There is no one success requests to ERS service and experiment is deleted. Expected: EXPERIMENT_DELETED
     */
    @Test
    public void testErsReportForNotSentAndDeletedExperiment() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experiment.setDeletedDate(LocalDateTime.now());
        experimentRepository.save(experiment);
        ExperimentResultsEntity experimentResultsEntity1 = TestHelperUtils.createExperimentResultsEntity(experiment);
        ExperimentResultsEntity experimentResultsEntity2 = TestHelperUtils.createExperimentResultsEntity(experiment);
        experimentResultsEntityRepository.saveAll(Arrays.asList(experimentResultsEntity1, experimentResultsEntity2));
        ExperimentErsReportDto experimentErsReportDto = ersService.getErsReport(experiment);
        Assertions.assertThat(experimentErsReportDto).isNotNull();
        Assertions.assertThat(experimentErsReportDto.getErsReportStatus().getValue()).isEqualTo(
                ErsReportStatus.EXPERIMENT_DELETED.name());
    }

    /**
     * There are success requests to ERS service and experiment is deleted. Expected: SUCCESS_SENT
     */
    @Test
    public void testErsReportForSentAndDeletedExperiment() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experiment.setDeletedDate(LocalDateTime.now());
        experimentRepository.save(experiment);
        ExperimentResultsEntity experimentResultsEntity1 = TestHelperUtils.createExperimentResultsEntity(experiment);
        ExperimentResultsEntity experimentResultsEntity2 = TestHelperUtils.createExperimentResultsEntity(experiment);
        experimentResultsEntityRepository.saveAll(Arrays.asList(experimentResultsEntity1, experimentResultsEntity2));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experimentResultsEntity1, experiment,
                        ErsResponseStatus.SUCCESS));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experimentResultsEntity2, experiment,
                        ErsResponseStatus.SUCCESS));
        ExperimentErsReportDto experimentErsReportDto = ersService.getErsReport(experiment);
        Assertions.assertThat(experimentErsReportDto).isNotNull();
        Assertions.assertThat(experimentErsReportDto.getErsReportStatus().getValue()).isEqualTo(
                ErsReportStatus.SUCCESS_SENT.name());
    }

    @Test
    public void testErsReportWithNeedSentStatus() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experimentRepository.save(experiment);
        ExperimentResultsEntity experimentResultsEntity1 = TestHelperUtils.createExperimentResultsEntity(experiment);
        ExperimentResultsEntity experimentResultsEntity2 = TestHelperUtils.createExperimentResultsEntity(experiment);
        experimentResultsEntityRepository.saveAll(Arrays.asList(experimentResultsEntity1, experimentResultsEntity2));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experimentResultsEntity1, experiment,
                        ErsResponseStatus.SUCCESS));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experimentResultsEntity2, experiment,
                        ErsResponseStatus.ERROR));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experimentResultsEntity2, experiment,
                        ErsResponseStatus.INVALID_REQUEST_ID));
        ExperimentErsReportDto experimentErsReportDto = ersService.getErsReport(experiment);
        Assertions.assertThat(experimentErsReportDto).isNotNull();
        Assertions.assertThat(experimentErsReportDto.getErsReportStatus().getValue()).isEqualTo(
                ErsReportStatus.NEED_SENT.name());
    }

    @Test
    public void testErsReportWithSuccessSentStatus() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experimentRepository.save(experiment);
        ExperimentResultsEntity experimentResultsEntity1 = TestHelperUtils.createExperimentResultsEntity(experiment);
        ExperimentResultsEntity experimentResultsEntity2 = TestHelperUtils.createExperimentResultsEntity(experiment);
        List<ExperimentResultsEntity> experimentResultsEntityList =
                Arrays.asList(experimentResultsEntity1, experimentResultsEntity2);
        experimentResultsEntityRepository.saveAll(experimentResultsEntityList);
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experimentResultsEntity1, experiment,
                        ErsResponseStatus.SUCCESS));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experimentResultsEntity2, experiment,
                        ErsResponseStatus.SUCCESS));
        ExperimentErsReportDto experimentErsReportDto = ersService.getErsReport(experiment);
        Assertions.assertThat(experimentErsReportDto).isNotNull();
        Assertions.assertThat(experimentErsReportDto.getErsReportStatus().getValue()).isEqualTo(
                ErsReportStatus.SUCCESS_SENT.name());
        Assertions.assertThat(experimentErsReportDto.getClassifiersCount()).isEqualTo(
                experimentResultsEntityList.size());
        Assertions.assertThat(experimentErsReportDto.getSentClassifiersCount()).isEqualTo(
                experimentResultsEntityList.size());
        Assertions.assertThat(experimentErsReportDto.getExperimentResults()).hasSameSizeAs(experimentResultsEntityList);
    }

    @Test
    public void testSentExperimentResults() throws Exception {
        ExperimentHistory experimentHistory = new ExperimentHistory();
        experimentHistory.setExperiment(newArrayList());
        experimentHistory.getExperiment().add(TestHelperUtils.getEvaluationResults());
        doNothing().when(ersRequestService).saveEvaluationResults(any(EvaluationResults.class), any(ErsRequest.class));
        ExperimentResultsEntity experimentResultsEntity = new ExperimentResultsEntity();
        experimentResultsEntity.setResultsIndex(0);
        ersService.sentExperimentResults(experimentResultsEntity, experimentHistory,
                ExperimentResultsRequestSource.MANUAL);
        verify(ersRequestService, times(experimentHistory.getExperiment().size())).saveEvaluationResults(
                any(EvaluationResults.class), any(ErsRequest.class));
    }

    @Test
    public void testGetEvaluationLogDetailsWithInProgressStatus() {
        EvaluationLog evaluationLog =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.NEW);
        EvaluationLogDetailsDto evaluationLogDetailsDto = ersService.getEvaluationLogDetails(evaluationLog);
        Assertions.assertThat(evaluationLogDetailsDto).isNotNull();
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsDto()).isNotNull();
        Assertions.assertThat(
                evaluationLogDetailsDto.getEvaluationResultsDto().getEvaluationResultsStatus().getValue()).isEqualTo(
                EvaluationResultsStatus.EVALUATION_IN_PROGRESS.name());
    }

    @Test
    public void testGetEvaluationLogDetailsWithEvaluationErrorStatus() {
        EvaluationLog evaluationLog =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.ERROR);
        EvaluationLogDetailsDto evaluationLogDetailsDto = ersService.getEvaluationLogDetails(evaluationLog);
        Assertions.assertThat(evaluationLogDetailsDto).isNotNull();
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsDto()).isNotNull();
        Assertions.assertThat(
                evaluationLogDetailsDto.getEvaluationResultsDto().getEvaluationResultsStatus().getValue()).isEqualTo(
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
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsDto()).isNotNull();
        Assertions.assertThat(
                evaluationLogDetailsDto.getEvaluationResultsDto().getEvaluationResultsStatus().getValue()).isEqualTo(
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
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsDto()).isNotNull();
        Assertions.assertThat(
                evaluationLogDetailsDto.getEvaluationResultsDto().getEvaluationResultsStatus().getValue()).isEqualTo(
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
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsDto()).isNotNull();
        Assertions.assertThat(
                evaluationLogDetailsDto.getEvaluationResultsDto().getEvaluationResultsStatus().getValue()).isEqualTo(
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
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsDto()).isNotNull();
        Assertions.assertThat(
                evaluationLogDetailsDto.getEvaluationResultsDto().getEvaluationResultsStatus().getValue()).isEqualTo(
                EvaluationResultsStatus.ERROR.name());
    }

    @Test
    public void testGetEvaluationResultsWithServiceUnavailable() {
        EvaluationLog evaluationLog = createAndSaveFinishedEvaluationLog();
        when(ersRequestService.getEvaluationResults(anyString())).thenThrow(new WebServiceIOException("I/O"));
        EvaluationLogDetailsDto evaluationLogDetailsDto = ersService.getEvaluationLogDetails(evaluationLog);
        Assertions.assertThat(evaluationLogDetailsDto).isNotNull();
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsDto()).isNotNull();
        Assertions.assertThat(
                evaluationLogDetailsDto.getEvaluationResultsDto().getEvaluationResultsStatus().getValue()).isEqualTo(
                EvaluationResultsStatus.ERS_SERVICE_UNAVAILABLE.name());
    }

    @Test
    public void testGetEvaluationResultsWithUnknownError() {
        EvaluationLog evaluationLog = createAndSaveFinishedEvaluationLog();
        when(ersRequestService.getEvaluationResults(anyString())).thenThrow(new RuntimeException());
        EvaluationLogDetailsDto evaluationLogDetailsDto = ersService.getEvaluationLogDetails(evaluationLog);
        Assertions.assertThat(evaluationLogDetailsDto).isNotNull();
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsDto()).isNotNull();
        Assertions.assertThat(
                evaluationLogDetailsDto.getEvaluationResultsDto().getEvaluationResultsStatus().getValue()).isEqualTo(
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
        Assertions.assertThat(
                evaluationLogDetailsDto.getEvaluationResultsDto().getEvaluationResultsStatus().getValue()).isEqualTo(
                EvaluationResultsStatus.RESULTS_RECEIVED.name());
    }

    /**
     * Case 1: There is no one request to ERS
     * Case 2: There is no one ERS request with status SUCCESS
     */
    @Test
    public void testGetExperimentResultsDetailsWithNotSentStatus() {
        //Case 1
        ExperimentResultsEntity experimentResultsEntity = createAndSaveExperimentResults();
        testGetExperimentResultsDetails(experimentResultsEntity, EvaluationResultsStatus.RESULTS_NOT_SENT);
        //Case 2
        ExperimentResultsRequest experimentResultsRequest =
                TestHelperUtils.createExperimentResultsRequest(experimentResultsEntity,
                        experimentResultsEntity.getExperiment(), ErsResponseStatus.ERROR);
        experimentResultsRequestRepository.save(experimentResultsRequest);
        testGetExperimentResultsDetails(experimentResultsEntity, EvaluationResultsStatus.RESULTS_NOT_SENT);
    }

    @Test
    public void testGetExperimentResultsDetailsWithResultsNotFoundStatus() {
        ExperimentResultsEntity experimentResultsEntity = createAndSaveExperimentResultsWithSuccessRequest();
        GetEvaluationResultsResponse response = new GetEvaluationResultsResponse();
        response.setStatus(ResponseStatus.RESULTS_NOT_FOUND);
        when(ersRequestService.getEvaluationResults(anyString())).thenReturn(response);
        testGetExperimentResultsDetails(experimentResultsEntity, EvaluationResultsStatus.EVALUATION_RESULTS_NOT_FOUND);
    }

    @Test
    public void testGetExperimentResultsDetailsWithResponseErrorStatus() {
        ExperimentResultsEntity experimentResultsEntity = createAndSaveExperimentResultsWithSuccessRequest();
        GetEvaluationResultsResponse response = new GetEvaluationResultsResponse();
        response.setStatus(ResponseStatus.ERROR);
        when(ersRequestService.getEvaluationResults(anyString())).thenReturn(response);
        testGetExperimentResultsDetails(experimentResultsEntity, EvaluationResultsStatus.ERROR);
    }

    @Test
    public void testGetExperimentResultsDetailsWithServiceUnavailable() {
        ExperimentResultsEntity experimentResultsEntity = createAndSaveExperimentResultsWithSuccessRequest();
        when(ersRequestService.getEvaluationResults(anyString())).thenThrow(new WebServiceIOException("I/O"));
        testGetExperimentResultsDetails(experimentResultsEntity, EvaluationResultsStatus.ERS_SERVICE_UNAVAILABLE);
    }

    @Test
    public void testGetExperimentResultsDetailsWithUnknownError() {
        ExperimentResultsEntity experimentResultsEntity = createAndSaveExperimentResultsWithSuccessRequest();
        when(ersRequestService.getEvaluationResults(anyString())).thenThrow(new RuntimeException());
        testGetExperimentResultsDetails(experimentResultsEntity, EvaluationResultsStatus.ERROR);
    }

    @Test
    public void testSuccessGetExperimentResultsDetails() {
        ExperimentResultsEntity experimentResultsEntity = createAndSaveExperimentResultsWithSuccessRequest();
        GetEvaluationResultsResponse evaluationResultsResponse = TestHelperUtils.createGetEvaluationResultsResponse
                (UUID.randomUUID().toString(), ResponseStatus.SUCCESS);
        evaluationResultsResponse.setStatistics(TestHelperUtils.createStatisticsReport());
        when(ersRequestService.getEvaluationResults(anyString())).thenReturn(evaluationResultsResponse);
        testGetExperimentResultsDetails(experimentResultsEntity, EvaluationResultsStatus.RESULTS_RECEIVED);
    }

    private ExperimentResultsEntity createAndSaveExperimentResults() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        ExperimentResultsEntity experimentResultsEntity = TestHelperUtils.createExperimentResultsEntity(experiment);
        experimentRepository.save(experiment);
        experimentResultsEntityRepository.save(experimentResultsEntity);
        return experimentResultsEntity;
    }

    private ExperimentResultsEntity createAndSaveExperimentResultsWithSuccessRequest() {
       ExperimentResultsEntity experimentResultsEntity = createAndSaveExperimentResults();
        ExperimentResultsRequest experimentResultsRequest =
                TestHelperUtils.createExperimentResultsRequest(experimentResultsEntity,
                        experimentResultsEntity.getExperiment(), ErsResponseStatus.SUCCESS);
        experimentResultsRequestRepository.save(experimentResultsRequest);
        return experimentResultsEntity;
    }

    private void testGetExperimentResultsDetails(ExperimentResultsEntity experimentResultsEntity,
                                                 EvaluationResultsStatus expectedStatus) {
        ExperimentResultsDetailsDto experimentResultsDetails =
                ersService.getExperimentResultsDetails(experimentResultsEntity);
        Assertions.assertThat(experimentResultsDetails).isNotNull();
        Assertions.assertThat(experimentResultsDetails.getEvaluationResultsDto()).isNotNull();
        Assertions.assertThat(
                experimentResultsDetails.getEvaluationResultsDto().getEvaluationResultsStatus().getValue()).isEqualTo(
                expectedStatus.name());
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
