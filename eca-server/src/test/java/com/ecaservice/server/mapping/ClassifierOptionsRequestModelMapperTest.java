package com.ecaservice.server.mapping;


import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.server.model.entity.ClassifierOptionsResponseModel;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.report.model.ClassifierOptionsRequestBean;
import com.ecaservice.web.dto.model.ClassifierOptionsRequestDto;
import eca.core.evaluation.EvaluationMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collections;

import static com.ecaservice.server.TestHelperUtils.createClassifierOptionsRequest;
import static com.ecaservice.server.TestHelperUtils.createClassifierOptionsRequestModel;
import static com.ecaservice.server.TestHelperUtils.createClassifierOptionsResponseModel;
import static com.ecaservice.server.TestHelperUtils.createInstancesInfo;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link ClassifierOptionsRequestModelMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({ClassifierOptionsRequestModelMapperImpl.class, DateTimeConverter.class, InstancesInfoMapperImpl.class})
class ClassifierOptionsRequestModelMapperTest {

    private static final String OPTIONS = "options";

    @Inject
    private ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper;

    @Test
    void testMapClassifierOptionsRequest() {
        ClassifierOptionsRequest request = createClassifierOptionsRequest();
        ClassifierOptionsRequestModel requestModel = classifierOptionsRequestModelMapper.map(request);
        assertThat(requestModel.getEvaluationMethod()).isEqualTo(EvaluationMethod.CROSS_VALIDATION);
        assertThat(requestModel.getNumFolds()).isEqualTo(request.getEvaluationMethodReport().getNumFolds().intValue());
        assertThat(requestModel.getNumTests()).isEqualTo(request.getEvaluationMethodReport().getNumTests().intValue());
        assertThat(requestModel.getSeed()).isEqualTo(request.getEvaluationMethodReport().getSeed().intValue());
    }

    @Test
    void testMapClassifierOptionsRequestModel() {
        ClassifierOptionsRequestModel requestModel =
                createClassifierOptionsRequestModel(TestHelperUtils.createInstancesInfo(), LocalDateTime.now(),
                        ErsResponseStatus.SUCCESS, Collections.singletonList(
                                createClassifierOptionsResponseModel(OPTIONS)));
        ClassifierOptionsRequestDto classifierOptionsRequestDto = classifierOptionsRequestModelMapper.map(requestModel);
        assertThat(classifierOptionsRequestDto).isNotNull();
        assertThat(classifierOptionsRequestDto.getRequestDate()).isEqualTo(requestModel.getRequestDate());
        assertThat(classifierOptionsRequestDto.getRequestId()).isEqualTo(requestModel.getRequestId());
        assertThat(classifierOptionsRequestDto.getResponseStatus().getDescription()).isEqualTo(
                requestModel.getResponseStatus().getDescription());
        assertThat(classifierOptionsRequestDto.getResponseStatus().getValue()).isEqualTo(
                requestModel.getResponseStatus().name());
        assertThat(classifierOptionsRequestDto.getInstancesInfo()).isNotNull();
        assertThat(classifierOptionsRequestDto.getInstancesInfo().getRelationName()).isEqualTo(
                requestModel.getInstancesInfo().getRelationName());
        assertThat(classifierOptionsRequestDto.getNumFolds()).isEqualTo(requestModel.getNumFolds());
        assertThat(classifierOptionsRequestDto.getNumTests()).isEqualTo(requestModel.getNumTests());
        assertThat(classifierOptionsRequestDto.getSeed()).isEqualTo(requestModel.getSeed());
        assertThat(classifierOptionsRequestDto.getEvaluationMethod().getDescription()).isEqualTo(
                requestModel.getEvaluationMethod().getDescription());
        assertThat(classifierOptionsRequestDto.getEvaluationMethod().getValue()).isEqualTo(
                requestModel.getEvaluationMethod().name());
    }

    @Test
    void testMapToClassifierOptionsRequestBean() {
        ClassifierOptionsResponseModel responseModel = createClassifierOptionsResponseModel(OPTIONS);
        ClassifierOptionsRequestModel requestModel =
                createClassifierOptionsRequestModel(createInstancesInfo(), LocalDateTime.now(),
                        ErsResponseStatus.SUCCESS,
                        Collections.singletonList(responseModel));
        ClassifierOptionsRequestBean classifierOptionsRequestBean =
                classifierOptionsRequestModelMapper.mapToBean(requestModel);
        assertThat(classifierOptionsRequestBean).isNotNull();
        assertThat(classifierOptionsRequestBean.getRequestDate()).isNotNull();
        assertThat(classifierOptionsRequestBean.getEvaluationMethod()).isNotNull();
        assertThat(classifierOptionsRequestBean.getRelationName()).isEqualTo(
                requestModel.getInstancesInfo().getRelationName());
        assertThat(classifierOptionsRequestBean.getRequestId()).isEqualTo(requestModel.getRequestId());
        assertThat(classifierOptionsRequestBean.getResponseStatus()).isEqualTo(
                requestModel.getResponseStatus().getDescription());
    }
}
