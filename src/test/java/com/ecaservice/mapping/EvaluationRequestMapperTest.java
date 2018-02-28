package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.EvaluationRequestDto;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.ecaservice.model.evaluation.EvaluationRequest;
import eca.metrics.KNearestNeighbours;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import weka.core.Instances;

import java.util.EnumMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks EvaluationRequestMapper functionality {@see EvaluationRequestMapper}.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(EvaluationRequestMapperImpl.class)
public class EvaluationRequestMapperTest {

    @Autowired
    private EvaluationRequestMapper evaluationRequestMapper;

    private Instances instances;

    @Before
    public void setUp() throws Exception {
        instances = TestHelperUtils.loadInstances();
    }

    @Test
    public void testEvaluationRequestWithTrainingSetMethod() {
        EvaluationRequestDto evaluationRequestDto = new EvaluationRequestDto();
        evaluationRequestDto.setClassifier(new KNearestNeighbours());
        evaluationRequestDto.setData(instances);
        evaluationRequestDto.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        EvaluationRequest evaluationRequest = evaluationRequestMapper.map(evaluationRequestDto);
        assertThat(evaluationRequest).isNotNull();
        assertThat(evaluationRequest.getInputData()).isNotNull();
        assertThat(evaluationRequest.getInputData().getClassifier()).isEqualTo(evaluationRequestDto.getClassifier());
        assertThat(evaluationRequest.getInputData().getData()).isEqualTo(evaluationRequestDto.getData());
        assertThat(evaluationRequest.getEvaluationMethod()).isEqualTo(evaluationRequestDto.getEvaluationMethod());
        assertThat(evaluationRequest.getEvaluationOptionsMap()).isEmpty();
    }

    @Test
    public void testEvaluationRequestWithCrossValidationMethod() {
        EvaluationRequestDto evaluationRequestDto = new EvaluationRequestDto();
        evaluationRequestDto.setClassifier(new KNearestNeighbours());
        evaluationRequestDto.setData(instances);
        evaluationRequestDto.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        evaluationRequestDto.setEvaluationOptionsMap(new EnumMap<>(EvaluationOption.class));
        evaluationRequestDto.getEvaluationOptionsMap().put(EvaluationOption.NUM_FOLDS,
                String.valueOf(TestHelperUtils.NUM_FOLDS));
        evaluationRequestDto.getEvaluationOptionsMap().put(EvaluationOption.NUM_TESTS,
                String.valueOf(TestHelperUtils.NUM_TESTS));
        EvaluationRequest evaluationRequest = evaluationRequestMapper.map(evaluationRequestDto);
        assertThat(evaluationRequest).isNotNull();
        assertThat(evaluationRequest.getInputData()).isNotNull();
        assertThat(evaluationRequest.getInputData().getClassifier()).isEqualTo(evaluationRequestDto.getClassifier());
        assertThat(evaluationRequest.getInputData().getData()).isEqualTo(evaluationRequestDto.getData());
        assertThat(evaluationRequest.getEvaluationMethod()).isEqualTo(evaluationRequestDto.getEvaluationMethod());
        assertThat(evaluationRequest.getEvaluationOptionsMap().get(EvaluationOption.NUM_FOLDS)).isEqualTo(
                evaluationRequestDto.getEvaluationOptionsMap().get(EvaluationOption.NUM_FOLDS));
        assertThat(evaluationRequest.getEvaluationOptionsMap().get(EvaluationOption.NUM_TESTS)).isEqualTo(
                evaluationRequestDto.getEvaluationOptionsMap().get(EvaluationOption.NUM_TESTS));
    }
}
