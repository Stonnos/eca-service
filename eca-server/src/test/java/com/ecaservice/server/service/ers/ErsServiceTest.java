package com.ecaservice.server.service.ers;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.ers.dto.ErsErrorCode;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.server.mapping.ClassificationCostsMapperImpl;
import com.ecaservice.server.mapping.GetEvaluationResultsMapper;
import com.ecaservice.server.mapping.GetEvaluationResultsMapperImpl;
import com.ecaservice.server.mapping.StatisticsReportMapperImpl;
import com.ecaservice.server.model.entity.ErsRequest;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import com.ecaservice.server.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.repository.ExperimentResultsEntityRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.core.evaluation.EvaluationResults;
import eca.dataminer.AbstractExperiment;
import feign.FeignException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.Collections;
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

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
    void testGetExperimentResultsDetailsWithResultsNotFoundStatus() throws JsonProcessingException {
        var badRequestEx = mock(FeignException.BadRequest.class);
        var validationError = new ValidationErrorDto();
        validationError.setCode(ErsErrorCode.RESULTS_NOT_FOUND.name());
        when(badRequestEx.contentUTF8()).thenReturn(
                OBJECT_MAPPER.writeValueAsString(Collections.singletonList(validationError)));
        when(ersRequestService.getEvaluationResults(anyString())).thenThrow(badRequestEx);
        assertEvaluationResults(EvaluationResultsStatus.EVALUATION_RESULTS_NOT_FOUND);
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
        String requestId = UUID.randomUUID().toString();
        GetEvaluationResultsResponse evaluationResultsResponse =
                TestHelperUtils.createGetEvaluationResultsResponse(requestId);
        when(ersRequestService.getEvaluationResults(anyString())).thenReturn(evaluationResultsResponse);
        assertEvaluationResults(EvaluationResultsStatus.RESULTS_RECEIVED);
    }

    private void assertEvaluationResults(EvaluationResultsStatus expectedStatus) {
        EvaluationResultsDto evaluationResultsDto =
                ersService.getEvaluationResultsFromErs(UUID.randomUUID().toString());
        Assertions.assertThat(evaluationResultsDto).isNotNull();
        Assertions.assertThat(evaluationResultsDto.getEvaluationResultsStatus().getValue()).isEqualTo(
                expectedStatus.name());
    }
}
