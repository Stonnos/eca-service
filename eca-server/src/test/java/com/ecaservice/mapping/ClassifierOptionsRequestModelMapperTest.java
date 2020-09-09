package com.ecaservice.mapping;


import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.entity.ClassifierOptionsResponseModel;
import com.ecaservice.model.entity.ErsResponseStatus;
import com.ecaservice.report.model.ClassifierOptionsRequestBean;
import com.ecaservice.web.dto.model.ClassifierOptionsRequestDto;
import eca.core.evaluation.EvaluationMethod;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Unit tests for checking {@link ClassifierOptionsRequestModelMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({ClassifierOptionsRequestModelMapperImpl.class, ErsEvaluationMethodMapperImpl.class,
        ClassifierOptionsResponseModelMapperImpl.class, DateTimeConverter.class})
class ClassifierOptionsRequestModelMapperTest {

    private static final String DATA_MD5_HASH = "hash";
    private static final String OPTIONS = "options";

    @Inject
    private ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper;

    @Test
    void testMapClassifierOptionsRequest() {
        ClassifierOptionsRequest request = TestHelperUtils.createClassifierOptionsRequest();
        ClassifierOptionsRequestModel requestModel = classifierOptionsRequestModelMapper.map(request);
        Assertions.assertThat(requestModel.getEvaluationMethod()).isEqualTo(EvaluationMethod.CROSS_VALIDATION);
        Assertions.assertThat(requestModel.getNumFolds()).isEqualTo(requestModel.getNumFolds());
        Assertions.assertThat(requestModel.getNumTests()).isEqualTo(requestModel.getNumTests());
        Assertions.assertThat(requestModel.getSeed()).isEqualTo(requestModel.getSeed());
    }

    @Test
    void testMapClassifierOptionsRequestModel() {
        ClassifierOptionsRequestModel requestModel =
                TestHelperUtils.createClassifierOptionsRequestModel(DATA_MD5_HASH, LocalDateTime.now(),
                        ErsResponseStatus.SUCCESS,
                        Collections.singletonList(TestHelperUtils.createClassifierOptionsResponseModel(OPTIONS)));
        ClassifierOptionsRequestDto classifierOptionsRequestDto = classifierOptionsRequestModelMapper.map(requestModel);
        Assertions.assertThat(classifierOptionsRequestDto).isNotNull();
        Assertions.assertThat(classifierOptionsRequestDto.getRequestDate()).isEqualTo(requestModel.getRequestDate());
        Assertions.assertThat(classifierOptionsRequestDto.getRequestId()).isEqualTo(requestModel.getRequestId());
        Assertions.assertThat(classifierOptionsRequestDto.getResponseStatus().getDescription()).isEqualTo(
                requestModel.getResponseStatus().getDescription());
        Assertions.assertThat(classifierOptionsRequestDto.getResponseStatus().getValue()).isEqualTo(
                requestModel.getResponseStatus().name());
        Assertions.assertThat(classifierOptionsRequestDto.getRelationName()).isEqualTo(requestModel.getRelationName());
        Assertions.assertThat(classifierOptionsRequestDto.getNumFolds()).isEqualTo(requestModel.getNumFolds());
        Assertions.assertThat(classifierOptionsRequestDto.getNumTests()).isEqualTo(requestModel.getNumTests());
        Assertions.assertThat(classifierOptionsRequestDto.getSeed()).isEqualTo(requestModel.getSeed());
        Assertions.assertThat(classifierOptionsRequestDto.getEvaluationMethod().getDescription()).isEqualTo(
                requestModel.getEvaluationMethod().getDescription());
        Assertions.assertThat(classifierOptionsRequestDto.getEvaluationMethod().getValue()).isEqualTo(
                requestModel.getEvaluationMethod().name());
        Assertions.assertThat(classifierOptionsRequestDto.getClassifierOptionsResponseModels()).hasSize(1);
    }

    @Test
    void testMapClassifierOptionsRequestModels() {
        ClassifierOptionsRequestModel requestModel =
                TestHelperUtils.createClassifierOptionsRequestModel(StringUtils.EMPTY, LocalDateTime.now(),
                        ErsResponseStatus.SUCCESS, Collections.emptyList());
        ClassifierOptionsRequestModel requestModel1 =
                TestHelperUtils.createClassifierOptionsRequestModel(StringUtils.EMPTY, LocalDateTime.now(),
                        ErsResponseStatus.SUCCESS, Collections.emptyList());
        List<ClassifierOptionsRequestDto> classifierOptionsRequestDtoList = classifierOptionsRequestModelMapper.map
                (Arrays.asList(requestModel, requestModel1));
        Assertions.assertThat(classifierOptionsRequestDtoList).hasSize(2);
    }

    @Test
    void testMapToClassifierOptionsRequestBean() {
        ClassifierOptionsResponseModel responseModel = TestHelperUtils.createClassifierOptionsResponseModel(OPTIONS);
        ClassifierOptionsRequestModel requestModel =
                TestHelperUtils.createClassifierOptionsRequestModel(DATA_MD5_HASH, LocalDateTime.now(),
                        ErsResponseStatus.SUCCESS,
                        Collections.singletonList(responseModel));
        ClassifierOptionsRequestBean classifierOptionsRequestBean =
                classifierOptionsRequestModelMapper.mapToBean(requestModel);
        Assertions.assertThat(classifierOptionsRequestBean).isNotNull();
        Assertions.assertThat(classifierOptionsRequestBean.getRequestDate()).isNotNull();
        Assertions.assertThat(classifierOptionsRequestBean.getEvaluationMethod()).isNotNull();
        Assertions.assertThat(classifierOptionsRequestBean.getRelationName()).isEqualTo(requestModel.getRelationName());
        Assertions.assertThat(classifierOptionsRequestBean.getRequestId()).isEqualTo(requestModel.getRequestId());
        Assertions.assertThat(classifierOptionsRequestBean.getResponseStatus()).isEqualTo(
                requestModel.getResponseStatus().getDescription());
        Assertions.assertThat(classifierOptionsRequestBean.getClassifierName()).isEqualTo(
                responseModel.getClassifierName());
        Assertions.assertThat(classifierOptionsRequestBean.getClassifierOptions()).isEqualTo(
                responseModel.getOptions());
    }
}
