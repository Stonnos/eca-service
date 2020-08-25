package com.ecaservice.mapping;

import com.ecaservice.classifier.options.model.DecisionTreeOptions;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.web.dto.model.ClassifierOptionsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for checking {@link ClassifierOptionsDatabaseModelMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(ClassifierOptionsDatabaseModelMapperImpl.class)
class ClassifierOptionsDatabaseModelMapperTest {

    private static final String CONFIG = "config";
    private static final long ID = 1L;
    private static final String CREATED_BY = "user";

    @Inject
    private ClassifierOptionsDatabaseModelMapper classifierOptionsDatabaseModelMapper;

    @Test
    void testMapClassifierOptionsDatabaseModel() {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = new ClassifierOptionsDatabaseModel();
        classifierOptionsDatabaseModel.setId(ID);
        classifierOptionsDatabaseModel.setConfig(CONFIG);
        classifierOptionsDatabaseModel.setCreationDate(LocalDateTime.now());
        classifierOptionsDatabaseModel.setOptionsName(DecisionTreeOptions.class.getSimpleName());
        classifierOptionsDatabaseModel.setCreatedBy(CREATED_BY);
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
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = new ClassifierOptionsDatabaseModel();
        classifierOptionsDatabaseModel.setCreationDate(LocalDateTime.now());
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel1 = new ClassifierOptionsDatabaseModel();
        classifierOptionsDatabaseModel1.setCreationDate(LocalDateTime.now().plusDays(1L));
        List<ClassifierOptionsDto> classifierOptionsDtoList = classifierOptionsDatabaseModelMapper.map(
                Arrays.asList(classifierOptionsDatabaseModel, classifierOptionsDatabaseModel1));
        assertThat(classifierOptionsDtoList).hasSize(2);
    }
}
