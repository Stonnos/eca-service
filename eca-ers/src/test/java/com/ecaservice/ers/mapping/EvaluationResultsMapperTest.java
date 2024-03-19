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
import static com.ecaservice.ers.TestHelperUtils.createEvaluationResultsInfo;
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
        assertThat(evaluationResultsInfo.getClassifierInfo()).isNotNull();
        assertThat(evaluationResultsInfo.getNumFolds().intValue()).isEqualTo(
                resultsRequest.getEvaluationMethodReport().getNumFolds().intValue());
        assertThat(evaluationResultsInfo.getNumTests().intValue()).isEqualTo(
                resultsRequest.getEvaluationMethodReport().getNumTests().intValue());
        assertThat(evaluationResultsInfo.getSeed().intValue()).isEqualTo(
                resultsRequest.getEvaluationMethodReport().getSeed().intValue());
    }

    @Test
    void testMapToEvaluationResultsHistoryToDto() {
        var evaluationResultsInfo = createEvaluationResultsInfo();
        var evaluationResultsHistoryDto =
                evaluationResultsMapper.mapToEvaluationResultsHistory(evaluationResultsInfo);
        assertThat(evaluationResultsHistoryDto).isNotNull();
        assertThat(evaluationResultsHistoryDto.getEvaluationMethod().getValue()).isEqualTo(
                evaluationResultsInfo.getEvaluationMethod().name());
        assertThat(evaluationResultsHistoryDto.getEvaluationMethod().getDescription()).isEqualTo(
                evaluationResultsInfo.getEvaluationMethod().getDescription());
        assertThat(evaluationResultsHistoryDto.getNumFolds()).isEqualTo(evaluationResultsInfo.getNumFolds());
        assertThat(evaluationResultsHistoryDto.getNumTests()).isEqualTo(evaluationResultsInfo.getNumTests());
        assertThat(evaluationResultsHistoryDto.getSeed()).isEqualTo(evaluationResultsInfo.getSeed());
        assertThat(evaluationResultsHistoryDto.getPctCorrect()).isEqualTo(
                evaluationResultsInfo.getStatistics().getPctCorrect());
        assertThat(evaluationResultsHistoryDto.getMaxAucValue()).isEqualTo(
                evaluationResultsInfo.getStatistics().getMaxAucValue());
        assertThat(evaluationResultsHistoryDto.getMeanAbsoluteError()).isEqualTo(
                evaluationResultsInfo.getStatistics().getMeanAbsoluteError());
        assertThat(evaluationResultsHistoryDto.getVarianceError()).isEqualTo(
                evaluationResultsInfo.getStatistics().getVarianceError());
        assertThat(evaluationResultsHistoryDto.getRootMeanSquaredError()).isEqualTo(
                evaluationResultsInfo.getStatistics().getRootMeanSquaredError());
        assertThat(evaluationResultsHistoryDto.getInstancesInfo()).isNotNull();
        assertThat(evaluationResultsHistoryDto.getInstancesInfo().getRelationName()).isEqualTo(
                evaluationResultsInfo.getInstancesInfo().getRelationName());
        assertThat(evaluationResultsHistoryDto.getInstancesInfo().getNumInstances()).isEqualTo(
                evaluationResultsInfo.getInstancesInfo().getNumInstances());
        assertThat(evaluationResultsHistoryDto.getInstancesInfo().getClassName()).isEqualTo(
                evaluationResultsInfo.getInstancesInfo().getClassName());
        assertThat(evaluationResultsHistoryDto.getInstancesInfo().getNumAttributes()).isEqualTo(
                evaluationResultsInfo.getInstancesInfo().getNumAttributes());
        assertThat(evaluationResultsHistoryDto.getInstancesInfo().getNumInstances()).isEqualTo(
                evaluationResultsInfo.getInstancesInfo().getNumInstances());
        assertThat(evaluationResultsHistoryDto.getInstancesInfo().getNumClasses()).isEqualTo(
                evaluationResultsInfo.getInstancesInfo().getNumClasses());
        assertThat(evaluationResultsHistoryDto.getInstancesInfo().getCreatedDate()).isEqualTo(
                evaluationResultsInfo.getInstancesInfo().getCreatedDate());
    }

    @Test
    void testMapToEvaluationResultsHistoryToReportBean() {
        var evaluationResultsInfo = createEvaluationResultsInfo();
        var evaluationResultsHistoryBean =
                evaluationResultsMapper.mapToEvaluationResultsHistoryBean(evaluationResultsInfo);
        assertThat(evaluationResultsHistoryBean).isNotNull();
        assertThat(evaluationResultsHistoryBean.getEvaluationMethod()).isNotNull();
        assertThat(evaluationResultsHistoryBean.getSaveDate()).isNotNull();
        assertThat(evaluationResultsHistoryBean.getPctCorrect()).isEqualTo(
                evaluationResultsInfo.getStatistics().getPctCorrect());
        assertThat(evaluationResultsHistoryBean.getMaxAucValue()).isEqualTo(
                evaluationResultsInfo.getStatistics().getMaxAucValue());
        assertThat(evaluationResultsHistoryBean.getMeanAbsoluteError()).isEqualTo(
                evaluationResultsInfo.getStatistics().getMeanAbsoluteError());
        assertThat(evaluationResultsHistoryBean.getVarianceError()).isEqualTo(
                evaluationResultsInfo.getStatistics().getVarianceError());
        assertThat(evaluationResultsHistoryBean.getRootMeanSquaredError()).isEqualTo(
                evaluationResultsInfo.getStatistics().getRootMeanSquaredError());
        assertThat(evaluationResultsHistoryBean.getRelationName()).isEqualTo(
                evaluationResultsInfo.getInstancesInfo().getRelationName());
        assertThat(evaluationResultsHistoryBean.getClassifierOptions())
                .isEqualTo(evaluationResultsInfo.getClassifierInfo().getOptions());
    }
}
