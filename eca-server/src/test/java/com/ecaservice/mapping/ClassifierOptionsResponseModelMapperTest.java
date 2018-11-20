package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.entity.ClassifierOptionsResponseModel;
import com.ecaservice.web.dto.model.ClassifierOptionsResponseDto;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link ClassifierOptionsResponseModelMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(ClassifierOptionsResponseModelMapperImpl.class)
public class ClassifierOptionsResponseModelMapperTest {

    @Inject
    private ClassifierOptionsResponseModelMapper classifierOptionsResponseModelMapper;

    @Test
    public void testMapClassifierOptionsResponseModel() {
        ClassifierOptionsResponseModel classifierOptionsResponseModel =
                TestHelperUtils.createClassifierOptionsResponseModel("Options");
        ClassifierOptionsResponseDto classifierOptionsResponseDto =
                classifierOptionsResponseModelMapper.map(classifierOptionsResponseModel);
        Assertions.assertThat(classifierOptionsResponseDto).isNotNull();
        Assertions.assertThat(classifierOptionsResponseDto.getClassifierName()).isEqualTo(
                classifierOptionsResponseModel.getClassifierName());
        Assertions.assertThat(classifierOptionsResponseDto.getOptions()).isEqualTo(
                classifierOptionsResponseModel.getOptions());
    }
}
