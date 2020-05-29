package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.web.dto.model.ClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.CreateClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.UpdateClassifiersConfigurationDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link ClassifiersConfigurationMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(ClassifiersConfigurationMapperImpl.class)
public class ClassifiersConfigurationMapperTest {

    private static final String CONFIGURATION_NAME = "ConfigurationName";
    private static final long ID = 1L;

    @Inject
    private ClassifiersConfigurationMapper classifiersConfigurationMapper;

    @Test
    void testMapCreateClassifiersConfigurationDto() {
        CreateClassifiersConfigurationDto classifiersConfigurationDto =
                new CreateClassifiersConfigurationDto(CONFIGURATION_NAME);
        ClassifiersConfiguration classifiersConfiguration =
                classifiersConfigurationMapper.map(classifiersConfigurationDto);
        assertThat(classifiersConfiguration).isNotNull();
        assertThat(classifiersConfiguration.getConfigurationName()).isEqualTo(
                classifiersConfigurationDto.getConfigurationName());
        assertThat(classifiersConfiguration.isBuildIn()).isFalse();
    }

    @Test
    void testMapUpdateClassifiersConfigurationDto() {
        UpdateClassifiersConfigurationDto classifiersConfigurationDto =
                new UpdateClassifiersConfigurationDto(ID, CONFIGURATION_NAME);
        ClassifiersConfiguration classifiersConfiguration = new ClassifiersConfiguration();
        classifiersConfigurationMapper.update(classifiersConfigurationDto, classifiersConfiguration);
        assertThat(classifiersConfiguration.getConfigurationName()).isEqualTo(
                classifiersConfigurationDto.getConfigurationName());
    }

    @Test
    void testMapClassifiersConfigurationEntity() {
        ClassifiersConfiguration classifiersConfiguration = TestHelperUtils.createClassifiersConfiguration();
        classifiersConfiguration.setId(ID);
        classifiersConfiguration.setUpdated(LocalDateTime.now());
        ClassifiersConfigurationDto classifiersConfigurationDto =
                classifiersConfigurationMapper.map(classifiersConfiguration);
        assertThat(classifiersConfigurationDto).isNotNull();
        assertThat(classifiersConfigurationDto.getId()).isEqualTo(classifiersConfiguration.getId());
        assertThat(classifiersConfigurationDto.getConfigurationName()).isEqualTo(
                classifiersConfiguration.getConfigurationName());
        assertThat(classifiersConfigurationDto.getCreationDate()).isEqualTo(classifiersConfiguration.getCreationDate());
    }
}
