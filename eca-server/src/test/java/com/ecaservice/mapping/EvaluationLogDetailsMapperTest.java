package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.evaluation.GetEvaluationResultsSimpleResponse;
import com.ecaservice.model.entity.ClassifierInputOptions;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks {@link EvaluationLogDetailsMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import({EvaluationLogDetailsMapperImpl.class, InstancesInfoMapperImpl.class,
        EvaluationLogInputOptionsMapperImpl.class, StatisticsReportMapperImpl.class})
public class EvaluationLogDetailsMapperTest {

    @Inject
    private EvaluationLogDetailsMapper evaluationLogDetailsMapper;

    @Test
    public void testMapToEvaluationLogDetailsDto() {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        evaluationLog.setEvaluationOptionsMap(
                TestHelperUtils.createEvaluationOptionsMap(TestHelperUtils.NUM_FOLDS, TestHelperUtils.NUM_TESTS));
        evaluationLog.setInstancesInfo(TestHelperUtils.createInstancesInfo());
        evaluationLog.setClassifierInputOptions(Collections.singletonList(new ClassifierInputOptions()));
        EvaluationLogDetailsDto evaluationLogDetailsDto = evaluationLogDetailsMapper.map(evaluationLog);
        assertThat(evaluationLogDetailsDto).isNotNull();
        assertThat(evaluationLogDetailsDto.getClassifierName()).isEqualTo(evaluationLog.getClassifierName());
        assertThat(evaluationLogDetailsDto.getCreationDate()).isEqualTo(evaluationLog.getCreationDate());
        assertThat(evaluationLogDetailsDto.getStartDate()).isEqualTo(evaluationLog.getStartDate());
        assertThat(evaluationLogDetailsDto.getEndDate()).isEqualTo(evaluationLog.getEndDate());
        assertThat(evaluationLogDetailsDto.getEvaluationMethod()).isEqualTo(
                evaluationLog.getEvaluationMethod().getDescription());
        assertThat(evaluationLogDetailsDto.getEvaluationStatus()).isEqualTo(
                evaluationLog.getEvaluationStatus().getDescription());
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
        GetEvaluationResultsSimpleResponse response = new GetEvaluationResultsSimpleResponse();
        response.setStatistics(TestHelperUtils.createStatisticsReport());
        evaluationLogDetailsMapper.update(response, evaluationLogDetailsDto);
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsDto()).isNotNull();
    }
}
