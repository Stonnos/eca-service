package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.evaluation.StatisticsReport;
import com.ecaservice.web.dto.model.EvaluationStatisticsDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

/**
 * Unit tests that checks {@link StatisticsReportMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(StatisticsReportMapperImpl.class)
public class StatisticsReportMapperTest {

    @Inject
    private StatisticsReportMapper statisticsReportMapper;

    @Test
    public void testMapStatisticsReport() {
        StatisticsReport statisticsReport = TestHelperUtils.createStatisticsReport();
        EvaluationStatisticsDto evaluationStatisticsDto = statisticsReportMapper.map(statisticsReport);
        Assertions.assertThat(evaluationStatisticsDto).isNotNull();
        Assertions.assertThat(evaluationStatisticsDto.getNumCorrect()).isEqualTo(statisticsReport.getNumCorrect());
        Assertions.assertThat(evaluationStatisticsDto.getNumIncorrect()).isEqualTo(statisticsReport.getNumIncorrect());
        Assertions.assertThat(evaluationStatisticsDto.getNumTestInstances()).isEqualTo(
                statisticsReport.getNumTestInstances());
        Assertions.assertThat(evaluationStatisticsDto.getPctCorrect()).isEqualTo(statisticsReport.getPctCorrect());
        Assertions.assertThat(evaluationStatisticsDto.getPctIncorrect()).isEqualTo(statisticsReport.getPctIncorrect());
        Assertions.assertThat(evaluationStatisticsDto.getMaxAucValue()).isEqualTo(statisticsReport.getMaxAucValue());
        Assertions.assertThat(evaluationStatisticsDto.getVarianceError()).isEqualTo(
                statisticsReport.getVarianceError());
        Assertions.assertThat(evaluationStatisticsDto.getMeanAbsoluteError()).isEqualTo(
                statisticsReport.getMeanAbsoluteError());
        Assertions.assertThat(evaluationStatisticsDto.getRootMeanSquaredError()).isEqualTo(
                statisticsReport.getRootMeanSquaredError());
        Assertions.assertThat(evaluationStatisticsDto.getConfidenceIntervalLowerBound()).isEqualTo(
                statisticsReport.getConfidenceIntervalLowerBound());
        Assertions.assertThat(evaluationStatisticsDto.getConfidenceIntervalUpperBound()).isEqualTo(
                statisticsReport.getConfidenceIntervalUpperBound());
    }
}
