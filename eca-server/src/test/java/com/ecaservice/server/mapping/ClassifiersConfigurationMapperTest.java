package com.ecaservice.server.mapping;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.server.report.model.ClassifiersConfigurationBean;
import com.ecaservice.web.dto.model.ClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.CreateClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.UpdateClassifiersConfigurationDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link ClassifiersConfigurationMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({ClassifiersConfigurationMapperImpl.class, DateTimeConverter.class})
class ClassifiersConfigurationMapperTest {

    private static final String CONFIGURATION_NAME = "ConfigurationName";
    private static final long ID = 1L;
    private static final String CREATED_BY = "user";

    @Autowired
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
        classifiersConfiguration.setCreatedBy(CREATED_BY);
        classifiersConfiguration.setUpdated(LocalDateTime.now());
        ClassifiersConfigurationDto classifiersConfigurationDto =
                classifiersConfigurationMapper.map(classifiersConfiguration);
        assertThat(classifiersConfigurationDto).isNotNull();
        assertThat(classifiersConfigurationDto.getId()).isEqualTo(classifiersConfiguration.getId());
        assertThat(classifiersConfigurationDto.getConfigurationName()).isEqualTo(
                classifiersConfiguration.getConfigurationName());
        assertThat(classifiersConfigurationDto.getCreationDate()).isEqualTo(classifiersConfiguration.getCreationDate());
        assertThat(classifiersConfigurationDto.getCreatedBy()).isEqualTo(classifiersConfiguration.getCreatedBy());
    }

    @Test
    void testMapToClassifiersConfigurationBean() {
        ClassifiersConfiguration classifiersConfiguration = TestHelperUtils.createClassifiersConfiguration();
        classifiersConfiguration.setId(ID);
        classifiersConfiguration.setCreatedBy(CREATED_BY);
        classifiersConfiguration.setUpdated(LocalDateTime.now());
        ClassifiersConfigurationBean classifiersConfigurationBean =
                classifiersConfigurationMapper.mapToBean(classifiersConfiguration);
        assertThat(classifiersConfigurationBean).isNotNull();
        assertThat(classifiersConfigurationBean.getConfigurationName()).isEqualTo(
                classifiersConfiguration.getConfigurationName());
        assertThat(classifiersConfigurationBean.getCreationDate()).isNotNull();
        assertThat(classifiersConfigurationBean.getUpdated()).isNotNull();
        assertThat(classifiersConfigurationBean.getCreatedBy()).isEqualTo(classifiersConfiguration.getCreatedBy());
        assertThat(classifiersConfigurationBean.isActive()).isEqualTo(classifiersConfiguration.isActive());
        assertThat(classifiersConfigurationBean.isBuildIn()).isEqualTo(classifiersConfiguration.isBuildIn());
    }
}
