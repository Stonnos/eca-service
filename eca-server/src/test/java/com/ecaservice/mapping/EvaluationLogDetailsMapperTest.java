package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.evaluation.GetEvaluationResultsResponse;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks {@link EvaluationLogDetailsMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import({EvaluationLogDetailsMapperImpl.class, InstancesInfoMapperImpl.class,
        ClassifierInputOptionsMapperImpl.class, StatisticsReportMapperImpl.class,
        ClassificationCostsMapperImpl.class})
public class EvaluationLogDetailsMapperTest {

    @Inject
    private EvaluationLogDetailsMapper evaluationLogDetailsMapper;

    @Test
    public void testMapToEvaluationLogDetailsDto() {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        evaluationLog.setEvaluationOptionsMap(
                TestHelperUtils.createEvaluationOptionsMap(TestHelperUtils.NUM_FOLDS, TestHelperUtils.NUM_TESTS));
        evaluationLog.setInstancesInfo(TestHelperUtils.createInstancesInfo());
        EvaluationLogDetailsDto evaluationLogDetailsDto = evaluationLogDetailsMapper.map(evaluationLog);
        assertThat(evaluationLogDetailsDto).isNotNull();
        assertThat(evaluationLogDetailsDto.getClassifierName()).isEqualTo(evaluationLog.getClassifierName());
        assertThat(evaluationLogDetailsDto.getCreationDate()).isEqualTo(evaluationLog.getCreationDate());
        assertThat(evaluationLogDetailsDto.getStartDate()).isEqualTo(evaluationLog.getStartDate());
        assertThat(evaluationLogDetailsDto.getEndDate()).isEqualTo(evaluationLog.getEndDate());
        assertThat(evaluationLogDetailsDto.getEvaluationMethod().getDescription()).isEqualTo(
                evaluationLog.getEvaluationMethod().getDescription());
        assertThat(evaluationLogDetailsDto.getEvaluationMethod().getValue()).isEqualTo(
                evaluationLog.getEvaluationMethod().name());
        assertThat(evaluationLogDetailsDto.getEvaluationStatus().getDescription()).isEqualTo(
                evaluationLog.getEvaluationStatus().getDescription());
        assertThat(evaluationLogDetailsDto.getEvaluationStatus().getValue()).isEqualTo(
                evaluationLog.getEvaluationStatus().name());
        assertThat(evaluationLogDetailsDto.getRequestId()).isEqualTo(evaluationLog.getRequestId());
        assertThat(evaluationLogDetailsDto.getInputOptions()).isNotNull();
        assertThat(evaluationLogDetailsDto.getInputOptions().size()).isOne();
        assertThat(evaluationLogDetailsDto.getNumFolds()).isNotNull();
        assertThat(evaluationLogDetailsDto.getNumTests()).isNotNull();
        assertThat(evaluationLogDetailsDto.getSeed()).isNull();
        assertThat(evaluationLogDetailsDto.getInstancesInfo()).isNotNull();
    }

    @Test
    public void testUpdateEvaluationLogDetailsDto() {
        EvaluationLogDetailsDto evaluationLogDetailsDto = new EvaluationLogDetailsDto();
        GetEvaluationResultsResponse response = new GetEvaluationResultsResponse();
        response.setStatistics(TestHelperUtils.createStatisticsReport());
        response.getClassificationCosts().add(TestHelperUtils.createClassificationCostsReport());
        response.getClassificationCosts().add(TestHelperUtils.createClassificationCostsReport());
        evaluationLogDetailsMapper.update(response, evaluationLogDetailsDto);
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsDto()).isNotNull();
        Assertions.assertThat(evaluationLogDetailsDto.getClassificationCosts()).hasSameSizeAs(
                response.getClassificationCosts());
    }
}
