package com.ecaservice.server.mapping;

import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.model.evaluation.EvaluationRequestDataModel;
import eca.core.evaluation.EvaluationMethod;
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
        EvaluationRequestDataModel evaluationRequestDataModel = evaluationRequestMapper.map(classifierOptionsRequest);
        assertThat(evaluationRequestDataModel.getEvaluationMethod()).isEqualTo(
                EvaluationMethod.CROSS_VALIDATION);
        assertThat(evaluationRequestDataModel.getNumFolds()).isEqualTo(
                classifierOptionsRequest.getEvaluationMethodReport().getNumFolds().intValue());
        assertThat(evaluationRequestDataModel.getNumTests()).isEqualTo(
                classifierOptionsRequest.getEvaluationMethodReport().getNumTests().intValue());
        assertThat(evaluationRequestDataModel.getSeed()).isEqualTo(
                classifierOptionsRequest.getEvaluationMethodReport().getSeed().intValue());
    }
}
