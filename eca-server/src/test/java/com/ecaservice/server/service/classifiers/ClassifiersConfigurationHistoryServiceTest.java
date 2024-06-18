package com.ecaservice.server.service.classifiers;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.message.template.service.MessageTemplateProcessor;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.mapping.ClassifiersConfigurationHistoryMapperImpl;
import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.server.model.entity.ClassifiersConfigurationActionType;
import com.ecaservice.server.model.entity.FilterTemplateType;
import com.ecaservice.server.repository.ClassifiersConfigurationHistoryRepository;
import com.ecaservice.server.repository.ClassifiersConfigurationRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.UserService;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.SortFieldRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.server.TestHelperUtils.createClassifiersConfiguration;
import static com.ecaservice.server.TestHelperUtils.createClassifiersConfigurationHistory;
import static com.ecaservice.server.model.entity.ClassifiersConfigurationHistoryEntity_.ACTION_TYPE;
import static com.ecaservice.server.model.entity.ClassifiersConfigurationHistoryEntity_.CREATED_AT;
import static com.ecaservice.server.model.entity.ClassifiersConfigurationHistoryEntity_.CREATED_BY;
import static com.ecaservice.server.model.entity.ClassifiersConfigurationHistoryEntity_.MESSAGE_TEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ClassifiersConfigurationHistoryService} functionality
 *
 * @author Roman Batygin
 */
@Import({AppProperties.class, ClassifiersConfigurationHistoryService.class,
        ClassifiersConfigurationHistoryMapperImpl.class})
class ClassifiersConfigurationHistoryServiceTest extends AbstractJpaTest {

    private static final String USER_NAME = "user";
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final String SEARCH_QUERY = "Создана";

    @Autowired
    private ClassifiersConfigurationHistoryRepository classifiersConfigurationHistoryRepository;
    @Autowired
    private ClassifiersConfigurationRepository classifiersConfigurationRepository;
    @Autowired
    private ClassifiersConfigurationHistoryService classifiersConfigurationHistoryService;
    @MockBean
    private FilterTemplateService filterTemplateService;
    @MockBean
    private UserService userService;
    @MockBean
    private MessageTemplateProcessor messageTemplateProcessor;
    @MockBean
    private ClassifiersFormTemplateProvider classifiersFormTemplateProvider;

    @Override
    public void init() {
        when(userService.getCurrentUser()).thenReturn(USER_NAME);
    }

    @Override
    public void deleteAll() {
        classifiersConfigurationHistoryRepository.deleteAll();
        classifiersConfigurationRepository.deleteAll();
    }

    @Test
    void testGetClassifiersConfigurationHistory() {
        when(filterTemplateService.getGlobalFilterFields(FilterTemplateType.CLASSIFIERS_CONFIGURATION_HISTORY))
                .thenReturn(List.of(CREATED_BY, MESSAGE_TEXT, ACTION_TYPE));
        var classifiersConfiguration = createAndSaveConfiguration();
        saveClassifiersConfigurationHistory(classifiersConfiguration,
                ClassifiersConfigurationActionType.CREATE_CONFIGURATION);
        saveClassifiersConfigurationHistory(classifiersConfiguration,
                ClassifiersConfigurationActionType.ADD_CLASSIFIER_OPTIONS);
        saveClassifiersConfigurationHistory(classifiersConfiguration,
                ClassifiersConfigurationActionType.REMOVE_CLASSIFIER_OPTIONS);
        saveClassifiersConfigurationHistory(classifiersConfiguration,
                ClassifiersConfigurationActionType.SET_ACTIVE);
        var pageRequestDto = new PageRequestDto(PAGE_NUMBER, PAGE_SIZE,
                Collections.singletonList(new SortFieldRequestDto(CREATED_AT, false)), SEARCH_QUERY,
                Collections.emptyList());
        var nextPage =
                classifiersConfigurationHistoryService.getNextPage(classifiersConfiguration.getId(), pageRequestDto);
        assertThat(nextPage).isNotNull();
        assertThat(nextPage.getPage()).isEqualTo(pageRequestDto.getPage());
        assertThat(nextPage.getTotalCount()).isOne();
        assertThat(nextPage.getContent()).hasSize(1);
        var next = nextPage.getContent().iterator().next();
        assertThat(next.getActionType().getValue()).isEqualTo(
                ClassifiersConfigurationActionType.CREATE_CONFIGURATION.name());
    }

    private ClassifiersConfiguration createAndSaveConfiguration() {
        var classifiersConfiguration = createClassifiersConfiguration();
        classifiersConfiguration.setBuildIn(false);
        return classifiersConfigurationRepository.save(classifiersConfiguration);
    }

    private void saveClassifiersConfigurationHistory(ClassifiersConfiguration classifiersConfiguration,
                                                     ClassifiersConfigurationActionType actionType) {
        var classifiersConfigurationHistory =
                createClassifiersConfigurationHistory(classifiersConfiguration, actionType, LocalDateTime.now());
        classifiersConfigurationHistoryRepository.save(classifiersConfigurationHistory);
    }
}
