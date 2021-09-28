package com.ecaservice.service.ers;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.mapping.ClassificationCostsMapperImpl;
import com.ecaservice.mapping.GetEvaluationResultsMapper;
import com.ecaservice.mapping.GetEvaluationResultsMapperImpl;
import com.ecaservice.mapping.StatisticsReportMapperImpl;
import com.ecaservice.model.entity.ErsRequest;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.repository.ExperimentResultsEntityRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import eca.core.evaluation.EvaluationResults;
import eca.dataminer.AbstractExperiment;
import feign.FeignException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ErsService} functionality.
 *
 * @author Roman Batygin
 */
@Import({StatisticsReportMapperImpl.class, ClassificationCostsMapperImpl.class, GetEvaluationResultsMapperImpl.class})
class ErsServiceTest extends AbstractJpaTest {

    @Mock
    private ErsRequestService ersRequestService;
    @Inject
    private GetEvaluationResultsMapper evaluationResultsMapper;
    @Inject
    private ExperimentResultsEntityRepository experimentResultsEntityRepository;
    @Inject
    private ExperimentRepository experimentRepository;

    private ErsService ersService;

    @Override
    public void init() {
        ersService = new ErsService(ersRequestService, evaluationResultsMapper);
    }

    @Override
    public void deleteAll() {
        experimentResultsEntityRepository.deleteAll();
        experimentRepository.deleteAll();
    }

    @Test
    void testSentExperimentResults() {
        AbstractExperiment experimentHistory = TestHelperUtils.createExperimentHistory();
        doNothing().when(ersRequestService).saveEvaluationResults(any(EvaluationResults.class), any(ErsRequest.class));
        ExperimentResultsEntity experimentResultsEntity = createExperimentResults();
        ersService.sentExperimentResults(experimentResultsEntity, experimentHistory,
                ExperimentResultsRequestSource.SYSTEM);
        verify(ersRequestService, atLeastOnce()).saveEvaluationResults(any(EvaluationResults.class),
                any(ErsRequest.class));
    }

    private ExperimentResultsEntity createExperimentResults() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experimentRepository.save(experiment);
        ExperimentResultsEntity experimentResultsEntity = new ExperimentResultsEntity();
        experimentResultsEntity.setResultsIndex(0);
        experimentResultsEntity.setExperiment(experiment);
        experimentResultsEntity.setClassifierInfo(TestHelperUtils.createClassifierInfo());
        return experimentResultsEntityRepository.save(experimentResultsEntity);
    }

    @Test
    void testGetExperimentResultsDetailsWithResultsNotFoundStatus() {
        testGetEvaluationResults(EvaluationResultsStatus.EVALUATION_RESULTS_NOT_FOUND);
    }

    @Test
    void testGetExperimentResultsDetailsWithServiceUnavailable() {
        FeignException.ServiceUnavailable serviceUnavailable = mock(FeignException.ServiceUnavailable.class);
        when(ersRequestService.getEvaluationResults(anyString())).thenThrow(serviceUnavailable);
        assertEvaluationResults(EvaluationResultsStatus.ERS_SERVICE_UNAVAILABLE);
    }

    @Test
    void testGetExperimentResultsDetailsWithUnknownError() {
        when(ersRequestService.getEvaluationResults(anyString())).thenThrow(new RuntimeException());
        assertEvaluationResults(EvaluationResultsStatus.ERROR);
    }

    @Test
    void testSuccessGetExperimentResultsDetails() {
        testGetEvaluationResults(EvaluationResultsStatus.RESULTS_RECEIVED);
    }

    private void testGetEvaluationResults(EvaluationResultsStatus expectedStatus) {
        String requestId = UUID.randomUUID().toString();
        GetEvaluationResultsResponse evaluationResultsResponse =
                TestHelperUtils.createGetEvaluationResultsResponse(requestId);
        when(ersRequestService.getEvaluationResults(anyString())).thenReturn(evaluationResultsResponse);
        assertEvaluationResults(expectedStatus);
    }

    private void assertEvaluationResults(EvaluationResultsStatus expectedStatus) {
        EvaluationResultsDto evaluationResultsDto =
                ersService.getEvaluationResultsFromErs(UUID.randomUUID().toString());
        Assertions.assertThat(evaluationResultsDto).isNotNull();
        Assertions.assertThat(evaluationResultsDto.getEvaluationResultsStatus().getValue()).isEqualTo(
                expectedStatus.name());
    }
}
