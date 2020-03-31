package com.ecaservice.service.classifiers;

import com.ecaservice.config.CommonConfig;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel_;
import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.repository.ClassifierOptionsDatabaseModelRepository;
import com.ecaservice.repository.ClassifiersConfigurationRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.junit.Test;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import javax.inject.Inject;
import java.util.Collections;

import static com.ecaservice.TestHelperUtils.createClassifierOptionsDatabaseModel;
import static com.ecaservice.TestHelperUtils.createClassifiersConfiguration;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link ClassifierOptionsService} functionality.
 *
 * @author Roman Batygin
 */
@Import({CommonConfig.class, ClassifierOptionsService.class})
public class ClassifierOptionsServiceTest extends AbstractJpaTest {

    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final String OPTIONS = "options";

    @Inject
    private ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository;
    @Inject
    private ClassifiersConfigurationRepository classifiersConfigurationRepository;
    @Inject
    private ClassifierOptionsService classifierOptionsService;

    @Override
    public void deleteAll() {
        classifierOptionsDatabaseModelRepository.deleteAll();
        classifiersConfigurationRepository.deleteAll();
    }

    @Test
    public void testGetConfigsPage() {
        ClassifiersConfiguration classifiersConfiguration = createClassifiersConfiguration();
        classifiersConfigurationRepository.save(classifiersConfiguration);
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel =
                createClassifierOptionsDatabaseModel(OPTIONS, classifiersConfiguration);
        classifierOptionsDatabaseModelRepository.save(classifierOptionsDatabaseModel);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, ClassifierOptionsDatabaseModel_.CREATION_DATE, false, null,
                        Collections.emptyList());
        Page<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModelPage =
                classifierOptionsService.getNextPage(pageRequestDto);
        assertThat(classifierOptionsDatabaseModelPage).isNotNull();
        assertThat(classifierOptionsDatabaseModelPage.getTotalElements()).isOne();
        assertThat(classifierOptionsDatabaseModelPage.getContent().size()).isEqualTo(pageRequestDto.getSize());
    }
}
