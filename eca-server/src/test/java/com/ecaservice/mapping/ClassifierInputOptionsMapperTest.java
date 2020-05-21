package com.ecaservice.mapping;

import com.ecaservice.model.entity.ClassifierInputOptions;
import com.ecaservice.web.dto.model.InputOptionDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link ClassifierInputOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(ClassifierInputOptionsMapperImpl.class)
public class ClassifierInputOptionsMapperTest {

    @Inject
    private ClassifierInputOptionsMapper classifierInputOptionsMapper;

    @Test
    void testMapInputOptions() {
        ClassifierInputOptions classifierInputOptions = new ClassifierInputOptions();
        classifierInputOptions.setOptionName("option");
        classifierInputOptions.setOptionValue("value");
        InputOptionDto inputOptionDto = classifierInputOptionsMapper.map(classifierInputOptions);
        Assertions.assertThat(inputOptionDto).isNotNull();
        Assertions.assertThat(inputOptionDto.getOptionName()).isEqualTo(classifierInputOptions.getOptionName());
        Assertions.assertThat(inputOptionDto.getOptionValue()).isEqualTo(classifierInputOptions.getOptionValue());
    }
}
