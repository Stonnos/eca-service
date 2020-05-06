package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.web.dto.model.ExperimentResultsDetailsDto;
import com.ecaservice.web.dto.model.ExperimentResultsDto;
import eca.core.evaluation.EvaluationResults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests that checks ExperimentResultsMapper functionality {@see ExperimentResultsMapper}.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({ExperimentResultsMapperImpl.class, ClassifierInfoMapperImpl.class,
        ClassifierInputOptionsMapperImpl.class, ExperimentMapperImpl.class})
public class ExperimentResultsMapperTest {

    @Inject
    private ExperimentResultsMapper experimentResultsMapper;


    @Test
    public void testMapEvaluationResultsToExperimentResults() {
        EvaluationResults evaluationResults = TestHelperUtils.getEvaluationResults();
        ExperimentResultsEntity experimentResultsEntity = experimentResultsMapper.map(evaluationResults);
        assertThat(experimentResultsEntity).isNotNull();
        assertThat(experimentResultsEntity.getClassifierInfo()).isNotNull();
        assertThat(experimentResultsEntity.getClassifierInfo().getClassifierName()).isEqualTo(
                evaluationResults.getClassifier().getClass().getSimpleName());
        assertThat(experimentResultsEntity.getClassifierInfo().getClassifierInputOptions()).isNotEmpty();
        assertThat(experimentResultsEntity.getPctCorrect()).isEqualTo(
                BigDecimal.valueOf(evaluationResults.getEvaluation().pctCorrect()));
    }

    @Test
    public void testMapToExperimentResultsDto() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        ExperimentResultsEntity experimentResultsEntity = TestHelperUtils.createExperimentResultsEntity(experiment);
        experimentResultsEntity.setId(1L);
        ExperimentResultsDto experimentResultsDto = experimentResultsMapper.map(experimentResultsEntity);
        assertExperimentResultsDto(experimentResultsDto, experimentResultsEntity);
    }

    @Test
    public void testMapToExperimentResultsDetailsDto() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        ExperimentResultsEntity experimentResultsEntity = TestHelperUtils.createExperimentResultsEntity(experiment);
        experimentResultsEntity.setId(1L);
        ExperimentResultsDetailsDto experimentResultsDetailsDto = experimentResultsMapper.mapDetails(experimentResultsEntity);
        assertExperimentResultsDto(experimentResultsDetailsDto, experimentResultsEntity);
        assertThat(experimentResultsDetailsDto.getExperimentDto()).isNotNull();
    }

    private void assertExperimentResultsDto(ExperimentResultsDto experimentResultsDto,
                                            ExperimentResultsEntity experimentResultsEntity) {
        assertThat(experimentResultsDto).isNotNull();
        assertThat(experimentResultsDto.getId()).isEqualTo(experimentResultsEntity.getId());
        assertThat(experimentResultsDto.getResultsIndex()).isEqualTo(experimentResultsEntity.getResultsIndex());
        assertThat(experimentResultsDto.getPctCorrect()).isEqualTo(experimentResultsEntity.getPctCorrect());
        assertThat(experimentResultsDto.getClassifierInfo()).isNotNull();
        assertThat(experimentResultsDto.getClassifierInfo().getClassifierName()).isEqualTo(
                experimentResultsEntity.getClassifierInfo().getClassifierName());
        assertThat(experimentResultsDto.getClassifierInfo().getInputOptions()).hasSameSizeAs(
                experimentResultsEntity.getClassifierInfo().getClassifierInputOptions());
    }
}
