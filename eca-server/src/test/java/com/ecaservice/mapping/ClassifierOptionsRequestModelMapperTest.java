package com.ecaservice.mapping;


import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.evaluation.EvaluationMethod;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link ClassifierOptionsRequestModelMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import({ClassifierOptionsRequestModelMapperImpl.class, ErsEvaluationMethodMapperImpl.class})
public class ClassifierOptionsRequestModelMapperTest {

    @Inject
    private ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper;

    @Test
    public void testMapClassifierOptionsRequest() {
        ClassifierOptionsRequest request = TestHelperUtils.createClassifierOptionsRequest();
        ClassifierOptionsRequestModel requestModel = classifierOptionsRequestModelMapper.map(request);
        Assertions.assertThat(requestModel.getEvaluationMethod()).isEqualTo(EvaluationMethod.CROSS_VALIDATION);
        Assertions.assertThat(requestModel.getNumFolds()).isEqualTo(requestModel.getNumFolds());
        Assertions.assertThat(requestModel.getNumTests()).isEqualTo(requestModel.getNumTests());
        Assertions.assertThat(requestModel.getSeed()).isEqualTo(requestModel.getSeed());
    }
}
