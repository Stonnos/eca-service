package com.ecaservice.service.experiment;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.mapping.ClassifierInfoMapperImpl;
import com.ecaservice.mapping.ClassifierInputOptionsMapperImpl;
import com.ecaservice.mapping.ExperimentMapperImpl;
import com.ecaservice.mapping.ExperimentResultsMapper;
import com.ecaservice.mapping.ExperimentResultsMapperImpl;
import com.ecaservice.model.entity.ErsResponseStatus;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.entity.ExperimentResultsRequest;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.repository.ExperimentResultsEntityRepository;
import com.ecaservice.repository.ExperimentResultsRequestRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.ers.ErsService;
import com.ecaservice.web.dto.model.ErsReportStatus;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import com.ecaservice.web.dto.model.ExperimentErsReportDto;
import com.ecaservice.web.dto.model.ExperimentResultsDetailsDto;
import eca.converters.model.ExperimentHistory;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ExperimentResultsService} functionality.
 *
 * @author Roman Batygin
 */
@Import({ClassifierInputOptionsMapperImpl.class, ExperimentResultsMapperImpl.class,
        ClassifierInfoMapperImpl.class, ExperimentMapperImpl.class})
public class ExperimentResultsServiceTest extends AbstractJpaTest {

    @Mock
    private ErsService ersService;
    @Mock
    private ExperimentResultsLockService lockService;
    @Inject
    private ExperimentResultsRequestRepository experimentResultsRequestRepository;
    @Inject
    private ExperimentResultsMapper experimentResultsMapper;
    @Inject
    private ExperimentRepository experimentRepository;
    @Inject
    private ExperimentResultsEntityRepository experimentResultsEntityRepository;

    private ExperimentResultsService experimentResultsService;

    @Override
    public void init() {
        experimentResultsService = new ExperimentResultsService(ersService, lockService, experimentResultsMapper,
                experimentResultsEntityRepository, experimentResultsRequestRepository);
    }

    @Override
    public void deleteAll() {
        experimentResultsRequestRepository.deleteAll();
        experimentResultsEntityRepository.deleteAll();
        experimentRepository.deleteAll();
    }

    @Test
    public void testSaveExperimentResultsForErsSent() throws Exception {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experimentRepository.save(experiment);
        ExperimentHistory experimentHistory = TestHelperUtils.createExperimentHistory();
        experimentResultsService.saveExperimentResultsToErsSent(experiment, experimentHistory);
        List<ExperimentResultsEntity> experimentResultsEntityList = experimentResultsEntityRepository.findAll();
        Assertions.assertThat(experimentResultsEntityList).hasSameSizeAs(experimentHistory.getExperiment());
    }

    @Test
    public void testErsReportWithExperimentInProgressStatus() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experimentRepository.save(experiment);
        testGetErsReport(experiment, ErsReportStatus.EXPERIMENT_IN_PROGRESS);
    }

    @Test
    public void testErsReportWithExperimentErrorStatus() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR);
        experimentRepository.save(experiment);
        testGetErsReport(experiment, ErsReportStatus.EXPERIMENT_ERROR);
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
        testGetErsReport(experiment, ErsReportStatus.EXPERIMENT_DELETED);
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
                TestHelperUtils.createExperimentResultsRequest(experimentResultsEntity1, ErsResponseStatus.SUCCESS));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experimentResultsEntity2, ErsResponseStatus.SUCCESS));
        testGetErsReport(experiment, ErsReportStatus.SUCCESS_SENT);
    }

    @Test
    public void testErsReportWithExperimentResultsNotFoundStatus() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experimentRepository.save(experiment);
        testGetErsReport(experiment, ErsReportStatus.EXPERIMENT_RESULTS_NOT_FOUND);
    }

    @Test
    public void testErsReportWithNeedSentStatus() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experimentRepository.save(experiment);
        ExperimentResultsEntity experimentResultsEntity1 = TestHelperUtils.createExperimentResultsEntity(experiment);
        ExperimentResultsEntity experimentResultsEntity2 = TestHelperUtils.createExperimentResultsEntity(experiment);
        experimentResultsEntityRepository.saveAll(Arrays.asList(experimentResultsEntity1, experimentResultsEntity2));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experimentResultsEntity1, ErsResponseStatus.SUCCESS));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experimentResultsEntity2, ErsResponseStatus.ERROR));
        experimentResultsRequestRepository.save(TestHelperUtils.createExperimentResultsRequest(experimentResultsEntity2,
                ErsResponseStatus.INVALID_REQUEST_ID));
        testGetErsReport(experiment, ErsReportStatus.NEED_SENT);
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
                TestHelperUtils.createExperimentResultsRequest(experimentResultsEntity1, ErsResponseStatus.SUCCESS));
        experimentResultsRequestRepository.save(
                TestHelperUtils.createExperimentResultsRequest(experimentResultsEntity2, ErsResponseStatus.SUCCESS));
        ExperimentErsReportDto experimentErsReportDto = experimentResultsService.getErsReport(experiment);
        Assertions.assertThat(experimentErsReportDto).isNotNull();
        Assertions.assertThat(experimentErsReportDto.getErsReportStatus().getValue()).isEqualTo(
                ErsReportStatus.SUCCESS_SENT.name());
        Assertions.assertThat(experimentErsReportDto.getClassifiersCount()).isEqualTo(
                experimentResultsEntityList.size());
        Assertions.assertThat(experimentErsReportDto.getSentClassifiersCount()).isEqualTo(
                experimentResultsEntityList.size());
        Assertions.assertThat(experimentErsReportDto.getExperimentResults()).hasSameSizeAs(experimentResultsEntityList);
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
                TestHelperUtils.createExperimentResultsRequest(experimentResultsEntity, ErsResponseStatus.ERROR);
        experimentResultsRequestRepository.save(experimentResultsRequest);
        testGetExperimentResultsDetails(experimentResultsEntity, EvaluationResultsStatus.RESULTS_NOT_SENT);
    }

    @Test
    public void testGetExperimentResultsDetailsWithResultsNotFoundStatus() {
        ExperimentResultsEntity experimentResultsEntity = createAndSaveExperimentResultsWithSuccessRequest();
        testGetExperimentResultsDetailsWithEvaluationResults(experimentResultsEntity,
                EvaluationResultsStatus.EVALUATION_RESULTS_NOT_FOUND);
    }

    @Test
    public void testGetExperimentResultsDetailsWithResponseErrorStatus() {
        ExperimentResultsEntity experimentResultsEntity = createAndSaveExperimentResultsWithSuccessRequest();
        testGetExperimentResultsDetailsWithEvaluationResults(experimentResultsEntity, EvaluationResultsStatus.ERROR);
    }

    @Test
    public void testGetExperimentResultsDetailsWithServiceUnavailable() {
        ExperimentResultsEntity experimentResultsEntity = createAndSaveExperimentResultsWithSuccessRequest();
        testGetExperimentResultsDetailsWithEvaluationResults(experimentResultsEntity,
                EvaluationResultsStatus.ERS_SERVICE_UNAVAILABLE);
    }

    @Test
    public void testSuccessGetExperimentResultsDetails() {
        ExperimentResultsEntity experimentResultsEntity = createAndSaveExperimentResultsWithSuccessRequest();
        testGetExperimentResultsDetailsWithEvaluationResults(experimentResultsEntity,
                EvaluationResultsStatus.RESULTS_RECEIVED);
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
                TestHelperUtils.createExperimentResultsRequest(experimentResultsEntity, ErsResponseStatus.SUCCESS);
        experimentResultsRequestRepository.save(experimentResultsRequest);
        return experimentResultsEntity;
    }

    private void testGetExperimentResultsDetails(ExperimentResultsEntity experimentResultsEntity,
                                                 EvaluationResultsStatus expectedStatus) {
        ExperimentResultsDetailsDto experimentResultsDetails =
                experimentResultsService.getExperimentResultsDetails(experimentResultsEntity);
        Assertions.assertThat(experimentResultsDetails).isNotNull();
        Assertions.assertThat(experimentResultsDetails.getEvaluationResultsDto()).isNotNull();
        Assertions.assertThat(
                experimentResultsDetails.getEvaluationResultsDto().getEvaluationResultsStatus().getValue()).isEqualTo(
                expectedStatus.name());
    }

    private void testGetExperimentResultsDetailsWithEvaluationResults(ExperimentResultsEntity experimentResultsEntity,
                                                                      EvaluationResultsStatus expectedStatus) {
        EvaluationResultsDto evaluationResultsDto = TestHelperUtils.createEvaluationResultsDto(expectedStatus);
        when(ersService.getEvaluationResultsFromErs(anyString())).thenReturn(evaluationResultsDto);
        testGetExperimentResultsDetails(experimentResultsEntity, expectedStatus);
    }

    private void testGetErsReport(Experiment experiment, ErsReportStatus expectedStatus) {
        ExperimentErsReportDto experimentErsReportDto = experimentResultsService.getErsReport(experiment);
        Assertions.assertThat(experimentErsReportDto).isNotNull();
        Assertions.assertThat(experimentErsReportDto.getErsReportStatus().getValue()).isEqualTo(expectedStatus.name());
    }
}
