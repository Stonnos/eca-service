package com.ecaservice.ers.mapping;

import com.ecaservice.ers.dto.EvaluationMethod;
import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.ers.model.EvaluationResultsInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.UUID;

import static com.ecaservice.ers.TestHelperUtils.buildEvaluationResultsReport;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link EvaluationResultsMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({EvaluationResultsMapperImpl.class, ClassificationCostsReportMapperImpl.class,
        ConfusionMatrixMapperImpl.class, StatisticsReportMapperImpl.class,
        InstancesMapperImpl.class, ClassifierReportMapperImpl.class,
        RocCurveReportMapperImpl.class, ClassifierOptionsInfoMapperImpl.class})
class EvaluationResultsMapperTest {

    @Inject
    private EvaluationResultsMapper evaluationResultsMapper;

    @Test
    void testMapEvaluationResultsReport() {
        EvaluationResultsRequest resultsRequest = buildEvaluationResultsReport(UUID.randomUUID().toString());
        EvaluationResultsInfo evaluationResultsInfo = evaluationResultsMapper.map(resultsRequest);
        assertThat(evaluationResultsInfo.getRequestId()).isEqualTo(resultsRequest.getRequestId());
        assertThat(evaluationResultsInfo.getEvaluationMethod()).isEqualTo(EvaluationMethod.CROSS_VALIDATION);
        assertThat(evaluationResultsInfo.getStatistics()).isNotNull();
        assertThat(evaluationResultsInfo.getClassificationCosts()).isNotNull();
        assertThat(evaluationResultsInfo.getClassificationCosts()).hasSameSizeAs(
                resultsRequest.getClassificationCosts());
        assertThat(evaluationResultsInfo.getConfusionMatrix()).isNotNull();
        assertThat(evaluationResultsInfo.getConfusionMatrix()).hasSameSizeAs(
                resultsRequest.getConfusionMatrix());
        assertThat(evaluationResultsInfo.getClassifierOptionsInfo()).isNotNull();
        assertThat(evaluationResultsInfo.getNumFolds().intValue()).isEqualTo(
                resultsRequest.getEvaluationMethodReport().getNumFolds().intValue());
        assertThat(evaluationResultsInfo.getNumTests().intValue()).isEqualTo(
                resultsRequest.getEvaluationMethodReport().getNumTests().intValue());
        assertThat(evaluationResultsInfo.getSeed().intValue()).isEqualTo(
                resultsRequest.getEvaluationMethodReport().getSeed().intValue());
    }
}
