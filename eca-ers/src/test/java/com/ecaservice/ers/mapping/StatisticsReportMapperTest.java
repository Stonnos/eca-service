package com.ecaservice.ers.mapping;

import com.ecaservice.ers.dto.StatisticsReport;
import com.ecaservice.ers.model.StatisticsInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.ers.TestHelperUtils.buildStatisticsInfo;
import static com.ecaservice.ers.TestHelperUtils.buildStatisticsReport;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link StatisticsReportMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(StatisticsReportMapperImpl.class)
class StatisticsReportMapperTest {

    @Inject
    private StatisticsReportMapper statisticsReportMapper;

    @Test
    void testStatisticsReportMap() {
        StatisticsReport report = buildStatisticsReport();
        StatisticsInfo statisticsInfo = statisticsReportMapper.map(report);
        assertThat(statisticsInfo.getNumTestInstances()).isEqualTo(report.getNumTestInstances().intValue());
        assertThat(statisticsInfo.getNumCorrect()).isEqualTo(report.getNumCorrect().intValue());
        assertThat(statisticsInfo.getNumIncorrect()).isEqualTo(report.getNumIncorrect().intValue());
        assertThat(statisticsInfo.getPctCorrect()).isEqualTo(report.getPctCorrect());
        assertThat(statisticsInfo.getPctIncorrect()).isEqualTo(report.getPctIncorrect());
        assertThat(statisticsInfo.getMeanAbsoluteError()).isEqualTo(report.getMeanAbsoluteError());
        assertThat(statisticsInfo.getRootMeanSquaredError()).isEqualTo(report.getRootMeanSquaredError());
        assertThat(statisticsInfo.getMaxAucValue()).isEqualTo(report.getMaxAucValue());
        assertThat(statisticsInfo.getVarianceError()).isEqualTo(report.getVarianceError());
        assertThat(statisticsInfo.getConfidenceIntervalLowerBound()).isEqualTo(
                report.getConfidenceIntervalLowerBound());
        assertThat(statisticsInfo.getConfidenceIntervalUpperBound()).isEqualTo(
                report.getConfidenceIntervalUpperBound());
    }

    @Test
    void testMapStatisticsInfo() {
        StatisticsInfo statisticsInfo = buildStatisticsInfo();
        StatisticsReport statisticsReport = statisticsReportMapper.map(statisticsInfo);
        assertThat(statisticsReport.getNumTestInstances().intValue()).isEqualTo(
                statisticsInfo.getNumTestInstances());
        assertThat(statisticsReport.getNumCorrect().intValue()).isEqualTo(statisticsInfo.getNumCorrect());
        assertThat(statisticsReport.getNumIncorrect().intValue()).isEqualTo(
                statisticsInfo.getNumIncorrect());
        assertThat(statisticsReport.getPctCorrect()).isEqualTo(statisticsInfo.getPctCorrect());
        assertThat(statisticsReport.getPctIncorrect()).isEqualTo(statisticsInfo.getPctIncorrect());
        assertThat(statisticsReport.getMeanAbsoluteError()).isEqualTo(statisticsInfo.getMeanAbsoluteError());
        assertThat(statisticsReport.getRootMeanSquaredError()).isEqualTo(
                statisticsInfo.getRootMeanSquaredError());
        assertThat(statisticsReport.getMaxAucValue()).isEqualTo(statisticsInfo.getMaxAucValue());
        assertThat(statisticsReport.getVarianceError()).isEqualTo(statisticsInfo.getVarianceError());
        assertThat(statisticsReport.getConfidenceIntervalLowerBound()).isEqualTo(
                statisticsInfo.getConfidenceIntervalLowerBound());
        assertThat(statisticsReport.getConfidenceIntervalUpperBound()).isEqualTo(
                statisticsInfo.getConfidenceIntervalUpperBound());
    }
}
