package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.dto.InstancesRequest;
import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import com.ecaservice.dto.evaluation.EvaluationMethod;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link ClassifierOptionsRequestMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({ClassifierOptionsRequestMapperImpl.class, CrossValidationConfig.class, InstancesConverter.class})
public class ClassifierOptionsRequestMapperTest {

    @Inject
    private ClassifierOptionsRequestMapper classifierOptionsRequestMapper;
    @Inject
    private CrossValidationConfig crossValidationConfig;

    private InstancesRequest instancesRequest;

    @Before
    public void init() throws Exception {
        instancesRequest = new InstancesRequest();
        instancesRequest.setData(TestHelperUtils.loadInstances());
    }

    @Test
    public void testMappingInstancesRequest() {
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
