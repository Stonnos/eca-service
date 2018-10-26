package com.ecaservice.mapping;

import com.ecaservice.dto.ClassifierOptionsDto;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for checking {@link ClassifierOptionsDatabaseModelMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ClassifierOptionsDatabaseModelMapperTest {

    @Inject
    private ClassifierOptionsDatabaseModelMapper classifierOptionsDatabaseModelMapper;

    @Test
    public void testMapClassifierOptionsDatabaseModel() {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = new ClassifierOptionsDatabaseModel();
        classifierOptionsDatabaseModel.setConfig("config");
        classifierOptionsDatabaseModel.setCreationDate(LocalDateTime.now());
        classifierOptionsDatabaseModel.setVersion(0);
        ClassifierOptionsDto classifierOptionsDto =
                classifierOptionsDatabaseModelMapper.map(classifierOptionsDatabaseModel);
        assertThat(classifierOptionsDto.getConfig()).isEqualTo(classifierOptionsDatabaseModel.getConfig());
        assertThat(classifierOptionsDto.getCreationDate()).isEqualTo(classifierOptionsDatabaseModel.getCreationDate());
        assertThat(classifierOptionsDto.getVersion()).isEqualTo(classifierOptionsDatabaseModel.getVersion());
    }
}
