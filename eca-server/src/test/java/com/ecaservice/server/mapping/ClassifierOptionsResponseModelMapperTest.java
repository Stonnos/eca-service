package com.ecaservice.server.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.server.model.entity.ClassifierOptionsResponseModel;
import com.ecaservice.web.dto.model.ClassifierOptionsResponseDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link ClassifierOptionsResponseModelMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(ClassifierOptionsResponseModelMapperImpl.class)
class ClassifierOptionsResponseModelMapperTest {

    @Inject
    private ClassifierOptionsResponseModelMapper classifierOptionsResponseModelMapper;

    @Test
    void testMapClassifierOptionsResponseModel() {
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
