package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.evaluation.StatisticsReport;
import com.ecaservice.model.entity.InstancesInfo;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.InstancesInfoDto;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks {@link StatisticsReportMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(StatisticsReportMapperImpl.class)
public class StatisticsReportMapperTest {

    @Inject
    private StatisticsReportMapper statisticsReportMapper;

    @Test
    public void testMapStatisticsReport() {
        StatisticsReport statisticsReport = TestHelperUtils.createStatisticsReport();
        EvaluationResultsDto evaluationResultsDto = statisticsReportMapper.map(statisticsReport);
        Assertions.assertThat(evaluationResultsDto).isNotNull();
        Assertions.assertThat(evaluationResultsDto.getNumCorrect()).isEqualTo(statisticsReport.getNumCorrect());
        Assertions.assertThat(evaluationResultsDto.getNumIncorrect()).isEqualTo(statisticsReport.getNumIncorrect());
        Assertions.assertThat(evaluationResultsDto.getNumTestInstances()).isEqualTo(
                statisticsReport.getNumTestInstances());
        Assertions.assertThat(evaluationResultsDto.getPctCorrect()).isEqualTo(statisticsReport.getPctCorrect());
        Assertions.assertThat(evaluationResultsDto.getPctIncorrect()).isEqualTo(statisticsReport.getPctIncorrect());
        Assertions.assertThat(evaluationResultsDto.getMaxAucValue()).isEqualTo(statisticsReport.getMaxAucValue());
        Assertions.assertThat(evaluationResultsDto.getVarianceError()).isEqualTo(statisticsReport.getVarianceError());
        Assertions.assertThat(evaluationResultsDto.getMeanAbsoluteError()).isEqualTo(
                statisticsReport.getMeanAbsoluteError());
        Assertions.assertThat(evaluationResultsDto.getRootMeanSquaredError()).isEqualTo(
                statisticsReport.getRootMeanSquaredError());
        Assertions.assertThat(evaluationResultsDto.getConfidenceIntervalLowerBound()).isEqualTo(
                statisticsReport.getConfidenceIntervalLowerBound());
        Assertions.assertThat(evaluationResultsDto.getConfidenceIntervalUpperBound()).isEqualTo(
                statisticsReport.getConfidenceIntervalUpperBound());
    }
}
