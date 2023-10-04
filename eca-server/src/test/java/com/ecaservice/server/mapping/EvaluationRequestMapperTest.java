package com.ecaservice.server.mapping;

import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.model.evaluation.EvaluationMessageRequestDataModel;
import com.ecaservice.server.model.evaluation.InstancesRequestDataModel;
import eca.core.evaluation.EvaluationMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks {@link  EvaluationRequestMapper}.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({EvaluationRequestMapperImpl.class, CrossValidationConfig.class})
class EvaluationRequestMapperTest {

    @Inject
    private EvaluationRequestMapper evaluationRequestMapper;
    @Inject
    private CrossValidationConfig crossValidationConfig;

    private InstancesRequestDataModel instancesRequestDataModel;

    @BeforeEach
    void init() {
        instancesRequestDataModel = new InstancesRequestDataModel(UUID.randomUUID().toString(), UUID.randomUUID().toString());
    }

    @Test
    void testMapClassifierOptionsRequest() {
        EvaluationMessageRequestDataModel evaluationRequestDataModel =
                evaluationRequestMapper.map(instancesRequestDataModel, crossValidationConfig);
        assertThat(evaluationRequestDataModel.getEvaluationMethod()).isEqualTo(
                EvaluationMethod.CROSS_VALIDATION);
        assertThat(evaluationRequestDataModel.getNumFolds()).isEqualTo(crossValidationConfig.getNumFolds().intValue());
        assertThat(evaluationRequestDataModel.getNumTests()).isEqualTo(crossValidationConfig.getNumTests().intValue());
        assertThat(evaluationRequestDataModel.getSeed()).isEqualTo(crossValidationConfig.getSeed().intValue());
    }
}
