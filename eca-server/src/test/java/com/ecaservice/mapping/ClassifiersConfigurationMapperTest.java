package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.web.dto.model.ClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.CreateClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.UpdateClassifiersConfigurationDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link ClassifiersConfigurationMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(ClassifiersConfigurationMapperImpl.class)
public class ClassifiersConfigurationMapperTest {

    private static final String CONFIGURATION_NAME = "ConfigurationName";
    private static final long ID = 1L;

    @Inject
    private ClassifiersConfigurationMapper classifiersConfigurationMapper;

    @Test
    public void testMapCreateClassifiersConfigurationDto() {
        CreateClassifiersConfigurationDto classifiersConfigurationDto = new CreateClassifiersConfigurationDto();
        classifiersConfigurationDto.setName(CONFIGURATION_NAME);
        ClassifiersConfiguration classifiersConfiguration =
                classifiersConfigurationMapper.map(classifiersConfigurationDto);
        assertThat(classifiersConfiguration).isNotNull();
        assertThat(classifiersConfiguration.getName()).isEqualTo(classifiersConfigurationDto.getName());
        assertThat(classifiersConfiguration.isBuildIn()).isFalse();
    }

    @Test
    public void testMapUpdateClassifiersConfigurationDto() {
        UpdateClassifiersConfigurationDto classifiersConfigurationDto = new UpdateClassifiersConfigurationDto();
        classifiersConfigurationDto.setName(CONFIGURATION_NAME);
        ClassifiersConfiguration classifiersConfiguration = new ClassifiersConfiguration();
        classifiersConfigurationMapper.update(classifiersConfigurationDto, classifiersConfiguration);
        assertThat(classifiersConfiguration.getName()).isEqualTo(classifiersConfigurationDto.getName());
    }

    @Test
    public void testMapClassifiersConfigurationEntity() {
        ClassifiersConfiguration classifiersConfiguration = TestHelperUtils.createClassifiersConfiguration();
        classifiersConfiguration.setId(ID);
        classifiersConfiguration.setUpdated(LocalDateTime.now());
        ClassifiersConfigurationDto classifiersConfigurationDto =
                classifiersConfigurationMapper.map(classifiersConfiguration);
        assertThat(classifiersConfigurationDto).isNotNull();
        assertThat(classifiersConfigurationDto.getId()).isEqualTo(classifiersConfiguration.getId());
        assertThat(classifiersConfigurationDto.getName()).isEqualTo(classifiersConfiguration.getName());
        assertThat(classifiersConfigurationDto.getCreated()).isEqualTo(classifiersConfiguration.getCreated());
    }
}
