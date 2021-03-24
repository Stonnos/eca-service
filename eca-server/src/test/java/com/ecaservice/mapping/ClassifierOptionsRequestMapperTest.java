package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.base.model.InstancesRequest;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.EvaluationMethod;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

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
        Assertions.assertThat(request.getEvaluationMethodReport()).isNotNull();
        Assertions.assertThat(request.getEvaluationMethodReport().getEvaluationMethod()).isEqualTo(
                EvaluationMethod.CROSS_VALIDATION);
        Assertions.assertThat(request.getEvaluationMethodReport().getNumFolds().intValue()).isEqualTo(
                crossValidationConfig.getNumFolds());
        Assertions.assertThat(request.getEvaluationMethodReport().getNumTests().intValue()).isEqualTo(
                crossValidationConfig.getNumTests());
        Assertions.assertThat(request.getEvaluationMethodReport().getSeed().intValue()).isEqualTo(
                crossValidationConfig.getSeed());
        Assertions.assertThat(request.getInstances()).isNotNull();
        Assertions.assertThat(request.getInstances().getRelationName()).isEqualTo(
                instancesRequest.getData().relationName());
        Assertions.assertThat(request.getInstances().getNumInstances().intValue()).isEqualTo(
                instancesRequest.getData().numInstances());
        Assertions.assertThat(request.getInstances().getNumAttributes().intValue()).isEqualTo(
                instancesRequest.getData().numAttributes());
        Assertions.assertThat(request.getInstances().getNumClasses().intValue()).isEqualTo(
                instancesRequest.getData().numClasses());
        Assertions.assertThat(request.getInstances().getClassName()).isEqualTo(
                instancesRequest.getData().classAttribute().name());
        Assertions.assertThat(request.getInstances().getXmlInstances()).isNotNull();
    }
}
