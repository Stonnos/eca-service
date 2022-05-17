package com.ecaservice.server.mapping;


import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.report.model.ClassifierOptionsRequestBean;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.server.model.entity.ClassifierOptionsResponseModel;
import com.ecaservice.server.model.entity.ErsResponseStatus;
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

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(requestModel.getEvaluationMethod()).isEqualTo(EvaluationMethod.CROSS_VALIDATION);
        assertThat(requestModel.getNumFolds()).isEqualTo(request.getEvaluationMethodReport().getNumFolds().intValue());
        assertThat(requestModel.getNumTests()).isEqualTo(request.getEvaluationMethodReport().getNumTests().intValue());
        assertThat(requestModel.getSeed()).isEqualTo(request.getEvaluationMethodReport().getSeed().intValue());
        assertThat(requestModel.getRelationName()).isEqualTo(request.getRelationName());
        assertThat(requestModel.getDataMd5Hash()).isEqualTo(request.getDataHash());
    }

    @Test
    void testMapClassifierOptionsRequestModel() {
        ClassifierOptionsRequestModel requestModel =
                TestHelperUtils.createClassifierOptionsRequestModel(DATA_MD5_HASH, LocalDateTime.now(),
                        ErsResponseStatus.SUCCESS,
                        Collections.singletonList(TestHelperUtils.createClassifierOptionsResponseModel(OPTIONS)));
        ClassifierOptionsRequestDto classifierOptionsRequestDto = classifierOptionsRequestModelMapper.map(requestModel);
        assertThat(classifierOptionsRequestDto).isNotNull();
        assertThat(classifierOptionsRequestDto.getRequestDate()).isEqualTo(requestModel.getRequestDate());
        assertThat(classifierOptionsRequestDto.getRequestId()).isEqualTo(requestModel.getRequestId());
        assertThat(classifierOptionsRequestDto.getResponseStatus().getDescription()).isEqualTo(
                requestModel.getResponseStatus().getDescription());
        assertThat(classifierOptionsRequestDto.getResponseStatus().getValue()).isEqualTo(
                requestModel.getResponseStatus().name());
        assertThat(classifierOptionsRequestDto.getRelationName()).isEqualTo(requestModel.getRelationName());
        assertThat(classifierOptionsRequestDto.getNumFolds()).isEqualTo(requestModel.getNumFolds());
        assertThat(classifierOptionsRequestDto.getNumTests()).isEqualTo(requestModel.getNumTests());
        assertThat(classifierOptionsRequestDto.getSeed()).isEqualTo(requestModel.getSeed());
        assertThat(classifierOptionsRequestDto.getEvaluationMethod().getDescription()).isEqualTo(
                requestModel.getEvaluationMethod().getDescription());
        assertThat(classifierOptionsRequestDto.getEvaluationMethod().getValue()).isEqualTo(
                requestModel.getEvaluationMethod().name());
        assertThat(classifierOptionsRequestDto.getClassifierOptionsResponseModels()).hasSize(1);
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
        assertThat(classifierOptionsRequestDtoList).hasSize(2);
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
        assertThat(classifierOptionsRequestBean).isNotNull();
        assertThat(classifierOptionsRequestBean.getRequestDate()).isNotNull();
        assertThat(classifierOptionsRequestBean.getEvaluationMethod()).isNotNull();
        assertThat(classifierOptionsRequestBean.getRelationName()).isEqualTo(requestModel.getRelationName());
        assertThat(classifierOptionsRequestBean.getRequestId()).isEqualTo(requestModel.getRequestId());
        assertThat(classifierOptionsRequestBean.getResponseStatus()).isEqualTo(
                requestModel.getResponseStatus().getDescription());
        assertThat(classifierOptionsRequestBean.getClassifierName()).isEqualTo(
                responseModel.getClassifierName());
        assertThat(classifierOptionsRequestBean.getClassifierOptions()).isEqualTo(
                responseModel.getOptions());
    }
}
