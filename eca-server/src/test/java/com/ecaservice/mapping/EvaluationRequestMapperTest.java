package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.EvaluationRequest;
import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import com.ecaservice.model.evaluation.EvaluationOption;
import eca.core.evaluation.EvaluationMethod;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks EvaluationRequestMapper functionality {@see EvaluationRequestMapper}.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import({EvaluationRequestMapperImpl.class, ErsEvaluationMethodMapperImpl.class})
public class EvaluationRequestMapperTest {

    @Inject
    private EvaluationRequestMapper evaluationRequestMapper;

    @Test
    public void testMapClassifierOptionsRequest() {
        ClassifierOptionsRequest classifierOptionsRequest = TestHelperUtils.createClassifierOptionsRequest();
        EvaluationRequest evaluationRequest =
                evaluationRequestMapper.map(classifierOptionsRequest);
        Assertions.assertThat(evaluationRequest.getEvaluationMethod()).isEqualTo(EvaluationMethod.CROSS_VALIDATION);
        assertThat(evaluationRequest.getEvaluationOptionsMap().get(EvaluationOption.NUM_FOLDS)).isEqualTo(
                classifierOptionsRequest.getEvaluationMethodReport().getNumFolds().toString());
        assertThat(evaluationRequest.getEvaluationOptionsMap().get(EvaluationOption.NUM_TESTS)).isEqualTo(
                classifierOptionsRequest.getEvaluationMethodReport().getNumTests().toString());
        assertThat(evaluationRequest.getEvaluationOptionsMap().get(EvaluationOption.SEED)).isEqualTo(
                classifierOptionsRequest.getEvaluationMethodReport().getSeed().toString());
    }
}
