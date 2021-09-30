package com.ecaservice.server.mapping;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.base.model.InstancesRequest;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.EvaluationMethod;
import com.ecaservice.server.config.CrossValidationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link ClassifierOptionsRequestMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({ClassifierOptionsRequestMapperImpl.class, CrossValidationConfig.class, InstancesConverter.class})
class ClassifierOptionsRequestMapperTest {

    @Inject
    private ClassifierOptionsRequestMapper classifierOptionsRequestMapper;
    @Inject
    private CrossValidationConfig crossValidationConfig;

    private InstancesRequest instancesRequest;

    @BeforeEach
    void init() {
        instancesRequest = new InstancesRequest();
        instancesRequest.setData(TestHelperUtils.loadInstances());
    }

    @Test
    void testMappingInstancesRequest() {
        ClassifierOptionsRequest request = classifierOptionsRequestMapper.map(instancesRequest, crossValidationConfig);
        assertThat(request.getEvaluationMethodReport()).isNotNull();
        assertThat(request.getEvaluationMethodReport().getEvaluationMethod()).isEqualTo(
                EvaluationMethod.CROSS_VALIDATION);
        assertThat(request.getEvaluationMethodReport().getNumFolds().intValue()).isEqualTo(
                crossValidationConfig.getNumFolds());
        assertThat(request.getEvaluationMethodReport().getNumTests().intValue()).isEqualTo(
                crossValidationConfig.getNumTests());
        assertThat(request.getEvaluationMethodReport().getSeed().intValue()).isEqualTo(
                crossValidationConfig.getSeed());
        assertThat(request.getRelationName()).isEqualTo(
                instancesRequest.getData().relationName());
        assertThat(request.getDataHash()).isNotNull();
    }
}
