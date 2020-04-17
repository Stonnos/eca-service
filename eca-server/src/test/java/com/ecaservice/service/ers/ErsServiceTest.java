package com.ecaservice.service.ers;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.evaluation.GetEvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.mapping.ClassificationCostsMapperImpl;
import com.ecaservice.mapping.GetEvaluationResultsMapper;
import com.ecaservice.mapping.GetEvaluationResultsMapperImpl;
import com.ecaservice.mapping.StatisticsReportMapperImpl;
import com.ecaservice.model.entity.ErsRequest;
import com.ecaservice.model.entity.ErsResponseStatus;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.entity.ExperimentResultsRequest;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.repository.ExperimentResultsEntityRepository;
import com.ecaservice.repository.ExperimentResultsRequestRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import eca.converters.model.ExperimentHistory;
import eca.core.evaluation.EvaluationResults;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.ws.client.WebServiceIOException;

import javax.inject.Inject;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ErsService} functionality.
 *
 * @author Roman Batygin
 */
@Import({StatisticsReportMapperImpl.class, ClassificationCostsMapperImpl.class, GetEvaluationResultsMapperImpl.class})
public class ErsServiceTest extends AbstractJpaTest {

    @Mock
    private ErsRequestService ersRequestService;
    @Inject
    private GetEvaluationResultsMapper evaluationResultsMapper;
    @Inject
    private ExperimentResultsRequestRepository experimentResultsRequestRepository;
    @Inject
    private ExperimentResultsEntityRepository experimentResultsEntityRepository;
    @Inject
    private ExperimentRepository experimentRepository;

    private ErsService ersService;

    @Override
    public void init() {
        ersService = new ErsService(ersRequestService, evaluationResultsMapper, experimentResultsRequestRepository);
    }

    @Override
    public void deleteAll() {
        experimentResultsRequestRepository.deleteAll();
        experimentResultsEntityRepository.deleteAll();
        experimentRepository.deleteAll();
    }

    @Test
    public void testSentExperimentResults() {
        ExperimentHistory experimentHistory = TestHelperUtils.createExperimentHistory();
        doNothing().when(ersRequestService).saveEvaluationResults(any(EvaluationResults.class), any(ErsRequest.class));
        ExperimentResultsEntity experimentResultsEntity = createExperimentResults();
        ersService.sentExperimentResults(experimentResultsEntity, experimentHistory,
                ExperimentResultsRequestSource.MANUAL);
        verify(ersRequestService, atLeastOnce()).saveEvaluationResults(any(EvaluationResults.class),
                any(ErsRequest.class));
    }

    @Test
    public void testAlreadySentExperimentResults() {
        ExperimentHistory experimentHistory = TestHelperUtils.createExperimentHistory();
        doNothing().when(ersRequestService).saveEvaluationResults(any(EvaluationResults.class), any(ErsRequest.class));
        ExperimentResultsEntity experimentResultsEntity = createExperimentResults();
        ExperimentResultsRequest experimentResultsRequest =
                TestHelperUtils.createExperimentResultsRequest(experimentResultsEntity, ErsResponseStatus.SUCCESS);
        experimentResultsRequestRepository.save(experimentResultsRequest);
        ersService.sentExperimentResults(experimentResultsEntity, experimentHistory,
                ExperimentResultsRequestSource.MANUAL);
        verify(ersRequestService, never()).saveEvaluationResults(any(EvaluationResults.class), any(ErsRequest.class));
    }

    private ExperimentResultsEntity createExperimentResults() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experimentRepository.save(experiment);
        ExperimentResultsEntity experimentResultsEntity = new ExperimentResultsEntity();
        experimentResultsEntity.setResultsIndex(0);
        experimentResultsEntity.setExperiment(experiment);
        return experimentResultsEntityRepository.save(experimentResultsEntity);
    }

    @Test
    public void testGetExperimentResultsDetailsWithResultsNotFoundStatus() {
        testGetEvaluationResults(ResponseStatus.RESULTS_NOT_FOUND,
                EvaluationResultsStatus.EVALUATION_RESULTS_NOT_FOUND);
    }

    @Test
    public void testGetExperimentResultsDetailsWithResponseErrorStatus() {
        testGetEvaluationResults(ResponseStatus.ERROR, EvaluationResultsStatus.ERROR);
    }

    @Test
    public void testGetExperimentResultsDetailsWithServiceUnavailable() {
        when(ersRequestService.getEvaluationResults(anyString())).thenThrow(new WebServiceIOException("I/O"));
        assertEvaluationResults(EvaluationResultsStatus.ERS_SERVICE_UNAVAILABLE);
    }

    @Test
    public void testGetExperimentResultsDetailsWithUnknownError() {
        when(ersRequestService.getEvaluationResults(anyString())).thenThrow(new RuntimeException());
        assertEvaluationResults(EvaluationResultsStatus.ERROR);
    }

    @Test
    public void testSuccessGetExperimentResultsDetails() {
        testGetEvaluationResults(ResponseStatus.SUCCESS, EvaluationResultsStatus.RESULTS_RECEIVED);
    }

    private void testGetEvaluationResults(ResponseStatus responseStatus, EvaluationResultsStatus expectedStatus) {
        String requestId = UUID.randomUUID().toString();
        GetEvaluationResultsResponse evaluationResultsResponse =
                TestHelperUtils.createGetEvaluationResultsResponse(requestId, responseStatus);
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
