package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.web.dto.model.ExperimentResultsDto;
import eca.core.evaluation.EvaluationResults;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests that checks ExperimentMapper functionality {@see ExperimentResultsMapper}.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import({ExperimentResultsMapperImpl.class, ClassifierInfoMapperImpl.class, ClassifierInputOptionsMapperImpl.class})
public class ExperimentResultsMapperTest {

    @Inject
    private ExperimentResultsMapper experimentResultsMapper;


    @Test
    public void testMapEvaluationResultsToExperimentResults() throws Exception {
        EvaluationResults evaluationResults = TestHelperUtils.getEvaluationResults();
        ExperimentResultsEntity experimentResultsEntity = experimentResultsMapper.map(evaluationResults);
        assertThat(experimentResultsEntity).isNotNull();
        assertThat(experimentResultsEntity.getPctCorrect()).isEqualTo(
                BigDecimal.valueOf(evaluationResults.getEvaluation().pctCorrect()));
    }

    @Test
    public void testMapToExperimentResultsDto() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        ExperimentResultsEntity experimentResultsEntity = TestHelperUtils.createExperimentResultsEntity(experiment);
        experimentResultsEntity.setId(1L);
        ExperimentResultsDto experimentResultsDetailsDto = experimentResultsMapper.map(experimentResultsEntity);
        assertThat(experimentResultsDetailsDto).isNotNull();
        assertThat(experimentResultsDetailsDto.getId()).isEqualTo(experimentResultsEntity.getId());
        assertThat(experimentResultsDetailsDto.getResultsIndex()).isEqualTo(experimentResultsEntity.getResultsIndex());
        assertThat(experimentResultsDetailsDto.getPctCorrect()).isEqualTo(experimentResultsEntity.getPctCorrect());
    }
}
