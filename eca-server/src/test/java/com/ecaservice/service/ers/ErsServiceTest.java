package com.ecaservice.service.ers;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.evaluation.GetEvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.mapping.ClassificationCostsMapperImpl;
import com.ecaservice.mapping.GetEvaluationResultsMapper;
import com.ecaservice.mapping.GetEvaluationResultsMapperImpl;
import com.ecaservice.mapping.StatisticsReportMapperImpl;
import com.ecaservice.model.entity.ErsRequest;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import eca.converters.model.ExperimentHistory;
import eca.core.evaluation.EvaluationResults;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ws.client.WebServiceIOException;

import javax.inject.Inject;
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
@RunWith(SpringRunner.class)
@Import({StatisticsReportMapperImpl.class, ClassificationCostsMapperImpl.class, GetEvaluationResultsMapperImpl.class})
public class ErsServiceTest {

    @Mock
    private ErsRequestService ersRequestService;
    @Inject
    private GetEvaluationResultsMapper evaluationResultsMapper;

    private ErsService ersService;

    @Before
    public void setUp() {
        ersService = new ErsService(ersRequestService, evaluationResultsMapper);
    }

    @Test
    public void testSentExperimentResults() throws Exception {
        ExperimentHistory experimentHistory = TestHelperUtils.createExperimentHistory();
        doNothing().when(ersRequestService).saveEvaluationResults(any(EvaluationResults.class), any(ErsRequest.class));
        ExperimentResultsEntity experimentResultsEntity = new ExperimentResultsEntity();
        experimentResultsEntity.setResultsIndex(0);
        ersService.sentExperimentResults(experimentResultsEntity, experimentHistory,
                ExperimentResultsRequestSource.MANUAL);
        verify(ersRequestService, times(experimentHistory.getExperiment().size())).saveEvaluationResults(
                any(EvaluationResults.class), any(ErsRequest.class));
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
