package com.ecaservice.mapping;

import com.ecaservice.model.entity.ClassifierInputOptions;
import com.ecaservice.web.dto.model.InputOptionDto;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link ClassifierInputOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(ClassifierInputOptionsMapperImpl.class)
public class ClassifierInputOptionsMapperTest {

    @Inject
    private ClassifierInputOptionsMapper classifierInputOptionsMapper;

    @Test
    public void testMapInputOptions() {
        ClassifierInputOptions classifierInputOptions = new ClassifierInputOptions();
        classifierInputOptions.setOptionName("option");
        classifierInputOptions.setOptionValue("value");
        InputOptionDto inputOptionDto = classifierInputOptionsMapper.map(classifierInputOptions);
        Assertions.assertThat(inputOptionDto).isNotNull();
        Assertions.assertThat(inputOptionDto.getOptionName()).isEqualTo(classifierInputOptions.getOptionName());
        Assertions.assertThat(inputOptionDto.getOptionValue()).isEqualTo(classifierInputOptions.getOptionValue());
    }
}
