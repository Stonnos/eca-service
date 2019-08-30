package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.evaluation.GetEvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks {@link GetEvaluationResultsMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import({StatisticsReportMapperImpl.class, ClassificationCostsMapperImpl.class, GetEvaluationResultsMapperImpl.class})
public class GetEvaluationResultsMapperTest {

    @Inject
    private GetEvaluationResultsMapper evaluationResultsMapper;

    @Test
    public void testMapEvaluationResultsResponseWithSuccessStatus() {
        GetEvaluationResultsResponse evaluationResultsResponse =
                TestHelperUtils.createGetEvaluationResultsResponse(UUID.randomUUID().toString(),
                        ResponseStatus.SUCCESS);
        evaluationResultsResponse.setStatistics(TestHelperUtils.createStatisticsReport());
        evaluationResultsResponse.getClassificationCosts().add(TestHelperUtils.createClassificationCostsReport());
        evaluationResultsResponse.getClassificationCosts().add(TestHelperUtils.createClassificationCostsReport());
        EvaluationResultsDto evaluationResultsDto = evaluationResultsMapper.map(evaluationResultsResponse);
        assertThat(evaluationResultsDto).isNotNull();
        assertThat(evaluationResultsDto.getEvaluationStatisticsDto()).isNotNull();
        assertThat(evaluationResultsDto.getClassificationCosts()).hasSameSizeAs(
                evaluationResultsResponse.getClassificationCosts());
        assertThat(evaluationResultsDto.getEvaluationResultsStatus().getValue()).isEqualTo(
                EvaluationResultsStatus.RESULTS_RECEIVED.name());
    }

    @Test
    public void testMapEvaluationResultsResponseWithResultsNotFound() {
        GetEvaluationResultsResponse evaluationResultsResponse =
                TestHelperUtils.createGetEvaluationResultsResponse(UUID.randomUUID().toString(),
                        ResponseStatus.RESULTS_NOT_FOUND);
        EvaluationResultsDto evaluationResultsDto = evaluationResultsMapper.map(evaluationResultsResponse);
        assertThat(evaluationResultsDto).isNotNull();
        assertThat(evaluationResultsDto.getEvaluationResultsStatus().getValue()).isEqualTo(
                EvaluationResultsStatus.EVALUATION_RESULTS_NOT_FOUND.name());
    }

    @Test
    public void testMapEvaluationResultsResponseWithError() {
        GetEvaluationResultsResponse evaluationResultsResponse =
                TestHelperUtils.createGetEvaluationResultsResponse(UUID.randomUUID().toString(),
                        ResponseStatus.ERROR);
        EvaluationResultsDto evaluationResultsDto = evaluationResultsMapper.map(evaluationResultsResponse);
        assertThat(evaluationResultsDto).isNotNull();
        assertThat(evaluationResultsDto.getEvaluationResultsStatus().getValue()).isEqualTo(
                EvaluationResultsStatus.ERROR.name());
    }
}
