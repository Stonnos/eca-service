package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.EvaluationRequestDto;
import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import com.ecaservice.model.InputData;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.ecaservice.model.evaluation.EvaluationRequest;
import eca.metrics.KNearestNeighbours;
import eca.trees.CART;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import weka.core.Instances;

import javax.inject.Inject;
import java.util.EnumMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks EvaluationRequestMapper functionality {@see EvaluationRequestMapper}.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EvaluationRequestMapperTest {

    @Inject
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

    @Test
    public void testMapClassifierOptionsRequest() {
        ClassifierOptionsRequest classifierOptionsRequest = TestHelperUtils.createClassifierOptionsRequest();
        InputData inputData = new InputData(new CART(), instances);
        EvaluationRequest evaluationRequest =
                evaluationRequestMapper.map(classifierOptionsRequest, inputData);
        Assertions.assertThat(evaluationRequest.getEvaluationMethod()).isEqualTo(EvaluationMethod.CROSS_VALIDATION);
        assertThat(evaluationRequest.getEvaluationOptionsMap().get(EvaluationOption.NUM_FOLDS)).isEqualTo(
                classifierOptionsRequest.getEvaluationMethodReport().getNumFolds().toString());
        assertThat(evaluationRequest.getEvaluationOptionsMap().get(EvaluationOption.NUM_TESTS)).isEqualTo(
                classifierOptionsRequest.getEvaluationMethodReport().getNumTests().toString());
        assertThat(evaluationRequest.getEvaluationOptionsMap().get(EvaluationOption.SEED)).isEqualTo(
                classifierOptionsRequest.getEvaluationMethodReport().getSeed().toString());
        assertThat(evaluationRequest.getInputData()).isNotNull();
        assertThat(evaluationRequest.getInputData()).isEqualTo(inputData);
    }
}
