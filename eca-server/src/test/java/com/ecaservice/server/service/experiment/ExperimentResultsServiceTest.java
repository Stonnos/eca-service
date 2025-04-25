package com.ecaservice.server.service.experiment;

import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.ExperimentMapperImpl;
import com.ecaservice.server.mapping.ExperimentResultsMapper;
import com.ecaservice.server.mapping.ExperimentResultsMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import com.ecaservice.server.model.entity.ExperimentResultsRequest;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.repository.ExperimentResultsEntityRepository;
import com.ecaservice.server.repository.ExperimentResultsRequestRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.classifiers.ClassifierOptionsInfoProcessor;
import com.ecaservice.server.service.ers.ErsService;
import com.ecaservice.web.dto.model.ErsReportStatus;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import com.ecaservice.web.dto.model.ExperimentErsReportDto;
import com.ecaservice.web.dto.model.ExperimentResultsDetailsDto;
import eca.dataminer.AbstractExperiment;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

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
@Import({ExperimentResultsMapperImpl.class, ExperimentMapperImpl.class,
        DateTimeConverter.class, InstancesInfoMapperImpl.class})
class ExperimentResultsServiceTest extends AbstractJpaTest {

    @Mock
    private ErsService ersService;
    @Mock
    private ClassifierOptionsInfoProcessor classifierOptionsInfoProcessor;
    @Mock
    private ClassifierOptionsAdapter classifierOptionsAdapter;
    @Autowired
    private ExperimentResultsRequestRepository experimentResultsRequestRepository;
    @Autowired
    private ExperimentResultsMapper experimentResultsMapper;
    @Autowired
    private ExperimentRepository experimentRepository;
    @Autowired
    private InstancesInfoRepository instancesInfoRepository;
    @Autowired
    private ExperimentResultsEntityRepository experimentResultsEntityRepository;

    private ExperimentResultsService experimentResultsService;

    @Override
    public void init() {
        experimentResultsService =
                new ExperimentResultsService(ersService, classifierOptionsInfoProcessor, experimentResultsMapper,
                        classifierOptionsAdapter, experimentResultsEntityRepository,
                        experimentResultsRequestRepository);
    }

    @Override
    public void deleteAll() {
        experimentResultsRequestRepository.deleteAll();
        experimentResultsEntityRepository.deleteAll();
        experimentRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testSaveExperimentResultsForErsSent() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        instancesInfoRepository.save(experiment.getInstancesInfo());
        experimentRepository.save(experiment);
        AbstractExperiment experimentHistory = TestHelperUtils.createExperimentHistory();
        experimentResultsService.saveExperimentResults(experiment, experimentHistory);
        List<ExperimentResultsEntity> experimentResultsEntityList = experimentResultsEntityRepository.findAll();
        Assertions.assertThat(experimentResultsEntityList).hasSameSizeAs(experimentHistory.getHistory());
    }

    @Test
    void testErsReportWithExperimentInProgressStatus() {
        Experiment experiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.IN_PROGRESS);
        experiment.setStartDate(LocalDateTime.now());
        instancesInfoRepository.save(experiment.getInstancesInfo());
        experimentRepository.save(experiment);
        testGetErsReport(experiment, ErsReportStatus.EXPERIMENT_IN_PROGRESS);
    }

    @Test
    void testErsReportWithNewExperimentStatus() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        instancesInfoRepository.save(experiment.getInstancesInfo());
        experimentRepository.save(experiment);
        testGetErsReport(experiment, ErsReportStatus.EXPERIMENT_NEW);
    }

    @Test
    void testErsReportWithExperimentErrorStatus() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR);
        instancesInfoRepository.save(experiment.getInstancesInfo());
        experimentRepository.save(experiment);
        testGetErsReport(experiment, ErsReportStatus.EXPERIMENT_ERROR);
    }

    @Test
    void testErsReportWithExperimentResultsNotFoundStatus() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        instancesInfoRepository.save(experiment.getInstancesInfo());
        experimentRepository.save(experiment);
        testGetErsReport(experiment, ErsReportStatus.EXPERIMENT_RESULTS_NOT_FOUND);
    }

    @Test
    void testErsReportWithExperimentResultsCanceledStatus() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.CANCELED);
        instancesInfoRepository.save(experiment.getInstancesInfo());
        experimentRepository.save(experiment);
        testGetErsReport(experiment, ErsReportStatus.EXPERIMENT_CANCELED);
    }

    @Test
    void testErsReportWithFetchedStatus() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        instancesInfoRepository.save(experiment.getInstancesInfo());
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
                ErsReportStatus.FETCHED.name());
        Assertions.assertThat(experimentErsReportDto.getExperimentResults()).hasSameSizeAs(experimentResultsEntityList);
    }

    /**
     * Case 1: There is no one request to ERS
     * Case 2: There is no one ERS request with status SUCCESS
     */
    @Test
    void testGetExperimentResultsDetailsWithNotSentStatus() {
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
    void testGetExperimentResultsDetailsWithResultsNotFoundStatus() {
        ExperimentResultsEntity experimentResultsEntity = createAndSaveExperimentResultsWithSuccessRequest();
        testGetExperimentResultsDetailsWithEvaluationResults(experimentResultsEntity,
                EvaluationResultsStatus.EVALUATION_RESULTS_NOT_FOUND);
    }

    @Test
    void testGetExperimentResultsDetailsWithResponseErrorStatus() {
        ExperimentResultsEntity experimentResultsEntity = createAndSaveExperimentResultsWithSuccessRequest();
        testGetExperimentResultsDetailsWithEvaluationResults(experimentResultsEntity, EvaluationResultsStatus.ERROR);
    }

    @Test
    void testGetExperimentResultsDetailsWithServiceUnavailable() {
        ExperimentResultsEntity experimentResultsEntity = createAndSaveExperimentResultsWithSuccessRequest();
        testGetExperimentResultsDetailsWithEvaluationResults(experimentResultsEntity,
                EvaluationResultsStatus.ERS_SERVICE_UNAVAILABLE);
    }

    @Test
    void testSuccessGetExperimentResultsDetails() {
        ExperimentResultsEntity experimentResultsEntity = createAndSaveExperimentResultsWithSuccessRequest();
        testGetExperimentResultsDetailsWithEvaluationResults(experimentResultsEntity,
                EvaluationResultsStatus.RESULTS_RECEIVED);
    }

    private ExperimentResultsEntity createAndSaveExperimentResults() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        ExperimentResultsEntity experimentResultsEntity = TestHelperUtils.createExperimentResultsEntity(experiment);
        instancesInfoRepository.save(experiment.getInstancesInfo());
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
