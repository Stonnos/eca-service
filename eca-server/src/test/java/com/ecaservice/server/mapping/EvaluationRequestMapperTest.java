package com.ecaservice.server.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import eca.core.evaluation.EvaluationMethod;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks EvaluationRequestMapper functionality {@see EvaluationRequestMapper}.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({EvaluationRequestMapperImpl.class, ErsEvaluationMethodMapperImpl.class})
class EvaluationRequestMapperTest {

    @Inject
    private EvaluationRequestMapper evaluationRequestMapper;

    @Test
    void testMapClassifierOptionsRequest() {
        ClassifierOptionsRequest classifierOptionsRequest = TestHelperUtils.createClassifierOptionsRequest();
        EvaluationRequest evaluationRequest =
                evaluationRequestMapper.map(classifierOptionsRequest);
        Assertions.assertThat(evaluationRequest.getEvaluationMethod()).isEqualTo(EvaluationMethod.CROSS_VALIDATION);
        assertThat(evaluationRequest.getNumFolds()).isEqualTo(
                classifierOptionsRequest.getEvaluationMethodReport().getNumFolds().intValue());
        assertThat(evaluationRequest.getNumTests()).isEqualTo(
                classifierOptionsRequest.getEvaluationMethodReport().getNumTests().intValue());
        assertThat(evaluationRequest.getSeed()).isEqualTo(
                classifierOptionsRequest.getEvaluationMethodReport().getSeed().intValue());
    }
}
