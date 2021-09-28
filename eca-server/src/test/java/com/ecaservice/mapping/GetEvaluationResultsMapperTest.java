package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks {@link GetEvaluationResultsMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({StatisticsReportMapperImpl.class, ClassificationCostsMapperImpl.class, GetEvaluationResultsMapperImpl.class})
class GetEvaluationResultsMapperTest {

    @Inject
    private GetEvaluationResultsMapper evaluationResultsMapper;

    @Test
    void testMapEvaluationResultsResponseWithSuccessStatus() {
        var evaluationResultsResponse = TestHelperUtils.createGetEvaluationResultsResponse(UUID.randomUUID().toString());
        evaluationResultsResponse.setStatistics(TestHelperUtils.createStatisticsReport());
        evaluationResultsResponse.setClassificationCosts(newArrayList());
        evaluationResultsResponse.getClassificationCosts().add(TestHelperUtils.createClassificationCostsReport());
        evaluationResultsResponse.getClassificationCosts().add(TestHelperUtils.createClassificationCostsReport());
        EvaluationResultsDto evaluationResultsDto = evaluationResultsMapper.map(evaluationResultsResponse);
        assertThat(evaluationResultsDto).isNotNull();
        assertThat(evaluationResultsDto.getEvaluationStatisticsDto()).isNotNull();
        assertThat(evaluationResultsDto.getClassificationCosts()).hasSameSizeAs(
                evaluationResultsResponse.getClassificationCosts());
    }
}
