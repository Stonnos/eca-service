package com.ecaservice.mapping;


import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.web.dto.model.ClassifierOptionsRequestDto;
import com.ecaservice.web.dto.model.ErsResponseStatus;
import eca.core.evaluation.EvaluationMethod;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

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
@RunWith(SpringRunner.class)
@Import({ClassifierOptionsRequestModelMapperImpl.class, ErsEvaluationMethodMapperImpl.class,
        ClassifierOptionsResponseModelMapperImpl.class, ErsResponseStatusMapperImpl.class})
public class ClassifierOptionsRequestModelMapperTest {

    @Inject
    private ErsResponseStatusMapper ersResponseStatusMapper;
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

    @Test
    public void testMapClassifierOptionsRequestModel() {
        ClassifierOptionsRequestModel requestModel =
                TestHelperUtils.createClassifierOptionsRequestModel("hash", LocalDateTime.now(), ResponseStatus.SUCCESS,
                        Collections.singletonList(TestHelperUtils.createClassifierOptionsResponseModel("options")));
        ClassifierOptionsRequestDto classifierOptionsRequestDto = classifierOptionsRequestModelMapper.map(requestModel);
        Assertions.assertThat(classifierOptionsRequestDto).isNotNull();
        Assertions.assertThat(classifierOptionsRequestDto.getRequestDate()).isEqualTo(requestModel.getRequestDate());
        Assertions.assertThat(classifierOptionsRequestDto.getRequestId()).isEqualTo(requestModel.getRequestId());
        ErsResponseStatus ersResponseStatus = ersResponseStatusMapper.map(requestModel.getResponseStatus());
        Assertions.assertThat(classifierOptionsRequestDto.getResponseStatus().getDescription()).isEqualTo(
                ersResponseStatus.getDescription());
        Assertions.assertThat(classifierOptionsRequestDto.getResponseStatus().getValue()).isEqualTo(
                ersResponseStatus.name());
        Assertions.assertThat(classifierOptionsRequestDto.getRelationName()).isEqualTo(requestModel.getRelationName());
        Assertions.assertThat(classifierOptionsRequestDto.getNumFolds()).isEqualTo(requestModel.getNumFolds());
        Assertions.assertThat(classifierOptionsRequestDto.getNumTests()).isEqualTo(requestModel.getNumTests());
        Assertions.assertThat(classifierOptionsRequestDto.getSeed()).isEqualTo(requestModel.getSeed());
        Assertions.assertThat(classifierOptionsRequestDto.getEvaluationMethod().getDescription()).isEqualTo(
                requestModel.getEvaluationMethod().getDescription());
        Assertions.assertThat(classifierOptionsRequestDto.getEvaluationMethod().getValue()).isEqualTo(
                requestModel.getEvaluationMethod().name());
        Assertions.assertThat(classifierOptionsRequestDto.getClassifierOptionsResponseModels()).isNotNull();
        Assertions.assertThat(classifierOptionsRequestDto.getClassifierOptionsResponseModels().size()).isOne();
    }

    @Test
    public void testMapClassifierOptionsRequestModels() {
        ClassifierOptionsRequestModel requestModel =
                TestHelperUtils.createClassifierOptionsRequestModel(StringUtils.EMPTY, LocalDateTime.now(),
                        ResponseStatus.SUCCESS, Collections.emptyList());
        ClassifierOptionsRequestModel requestModel1 =
                TestHelperUtils.createClassifierOptionsRequestModel(StringUtils.EMPTY, LocalDateTime.now(),
                        ResponseStatus.SUCCESS, Collections.emptyList());
        List<ClassifierOptionsRequestDto> classifierOptionsRequestDtoList = classifierOptionsRequestModelMapper.map
                (Arrays.asList(requestModel, requestModel1));
        Assertions.assertThat(classifierOptionsRequestDtoList).isNotNull();
        Assertions.assertThat(classifierOptionsRequestDtoList).hasSize(2);
    }
}
