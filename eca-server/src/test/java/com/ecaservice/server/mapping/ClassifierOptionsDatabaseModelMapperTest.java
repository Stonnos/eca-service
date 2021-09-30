package com.ecaservice.server.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.report.model.ClassifierOptionsBean;
import com.ecaservice.server.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.web.dto.model.ClassifierOptionsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for checking {@link ClassifierOptionsDatabaseModelMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({ClassifierOptionsDatabaseModelMapperImpl.class, DateTimeConverter.class})
class ClassifierOptionsDatabaseModelMapperTest {

    private static final long ID = 1L;

    @Inject
    private ClassifierOptionsDatabaseModelMapper classifierOptionsDatabaseModelMapper;

    @Test
    void testMapClassifierOptionsDatabaseModel() {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel =
                TestHelperUtils.createClassifierOptionsDatabaseModel();
        classifierOptionsDatabaseModel.setId(ID);
        ClassifierOptionsDto classifierOptionsDto =
                classifierOptionsDatabaseModelMapper.map(classifierOptionsDatabaseModel);
        assertThat(classifierOptionsDto).isNotNull();
        assertThat(classifierOptionsDto.getId()).isEqualTo(classifierOptionsDatabaseModel.getId());
        assertThat(classifierOptionsDto.getConfig()).isEqualTo(classifierOptionsDatabaseModel.getConfig());
        assertThat(classifierOptionsDto.getCreationDate()).isEqualTo(classifierOptionsDatabaseModel.getCreationDate());
        assertThat(classifierOptionsDto.getOptionsName()).isEqualTo(classifierOptionsDatabaseModel.getOptionsName());
        assertThat(classifierOptionsDto.getCreatedBy()).isEqualTo(classifierOptionsDatabaseModel.getCreatedBy());
    }

    @Test
    void testMapClassifierOptionsDatabaseModelsList() {
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels =
                Arrays.asList(TestHelperUtils.createClassifierOptionsDatabaseModel(),
                        TestHelperUtils.createClassifierOptionsDatabaseModel());
        List<ClassifierOptionsDto> classifierOptionsDtoList =
                classifierOptionsDatabaseModelMapper.map(classifierOptionsDatabaseModels);
        assertThat(classifierOptionsDtoList).hasSameSizeAs(classifierOptionsDatabaseModels);
    }

    @Test
    void testMapToClassifierOptionsBean() {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel =
                TestHelperUtils.createClassifierOptionsDatabaseModel();
        ClassifierOptionsBean classifiersConfigurationBean =
                classifierOptionsDatabaseModelMapper.mapToBean(classifierOptionsDatabaseModel);
        assertThat(classifiersConfigurationBean).isNotNull();
        assertThat(classifiersConfigurationBean.getConfig()).isEqualTo(classifierOptionsDatabaseModel.getConfig());
        assertThat(classifiersConfigurationBean.getCreatedBy()).isEqualTo(
                classifierOptionsDatabaseModel.getCreatedBy());
        assertThat(classifiersConfigurationBean.getOptionsName()).isEqualTo(
                classifierOptionsDatabaseModel.getOptionsName());
        assertThat(classifiersConfigurationBean.getCreationDate()).isNotNull();
    }
}
