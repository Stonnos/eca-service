package com.ecaservice.core.filter.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.filter.AbstractJpaTest;
import com.ecaservice.core.filter.TestHelperUtils;
import com.ecaservice.core.filter.entity.FilterDictionary;
import com.ecaservice.core.filter.entity.FilterTemplate;
import com.ecaservice.core.filter.entity.GlobalFilterTemplate;
import com.ecaservice.core.filter.mapping.FilterDictionaryMapperImpl;
import com.ecaservice.core.filter.mapping.FilterDictionaryValueMapperImpl;
import com.ecaservice.core.filter.mapping.FilterFieldMapperImpl;
import com.ecaservice.core.filter.repository.FilterDictionaryRepository;
import com.ecaservice.core.filter.repository.FilterTemplateRepository;
import com.ecaservice.core.filter.repository.GlobalFilterTemplateRepository;
import com.ecaservice.web.dto.model.FilterDictionaryDto;
import com.ecaservice.web.dto.model.FilterFieldDto;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for checking {@link FilterService} functionality.
 *
 * @author Roman Batygin
 */
@Import({FilterFieldMapperImpl.class, FilterDictionaryMapperImpl.class, FilterDictionaryValueMapperImpl.class,
        FilterService.class})
class FilterServiceTest extends AbstractJpaTest {

    public static final String FILTER_TEMPLATE_TYPE = "template";
    public static final String DICTIONARY_NAME = "dictionaryName";
    @Inject
    private FilterTemplateRepository filterTemplateRepository;
    @Inject
    private GlobalFilterTemplateRepository globalFilterTemplateRepository;
    @Inject
    private FilterDictionaryRepository filterDictionaryRepository;

    @Inject
    private FilterService filterService;

    @Override
    public void deleteAll() {
        filterTemplateRepository.deleteAll();
        globalFilterTemplateRepository.deleteAll();
        filterDictionaryRepository.deleteAll();
    }

    @Test
    void testGetFilterTemplateFields() {
        FilterTemplate filterTemplate = TestHelperUtils.createFilterTemplate(FILTER_TEMPLATE_TYPE);
        filterTemplateRepository.save(filterTemplate);
        List<FilterFieldDto> filterFieldDtoList = filterService.getFilterFields(FILTER_TEMPLATE_TYPE);
        assertThat(filterFieldDtoList).hasSameSizeAs(filterTemplate.getFields());
    }

    @Test
    void testNotExistingFilterTemplate() {
        assertThrows(EntityNotFoundException.class,
                () -> filterService.getFilterFields(FILTER_TEMPLATE_TYPE));
    }

    @Test
    void testGetGlobalFilterTemplateFields() {
        GlobalFilterTemplate filterTemplate =
                TestHelperUtils.createGlobalFilterTemplate(FILTER_TEMPLATE_TYPE);
        globalFilterTemplateRepository.save(filterTemplate);
        List<String> fields = filterService.getGlobalFilterFields(FILTER_TEMPLATE_TYPE);
        assertThat(fields).hasSameSizeAs(filterTemplate.getFields());
    }

    @Test
    void testNotExistingGlobalFilterTemplate() {
        assertThrows(EntityNotFoundException.class,
                () -> filterService.getGlobalFilterFields(FILTER_TEMPLATE_TYPE));
    }

    @Test
    void testGetFilterDictionary() {
        FilterDictionary filterDictionary = new FilterDictionary();
        filterDictionary.setName(DICTIONARY_NAME);
        filterDictionaryRepository.save(filterDictionary);
        FilterDictionaryDto filterDictionaryDto =
                filterService.getFilterDictionary(DICTIONARY_NAME);
        assertThat(filterDictionaryDto).isNotNull();
        assertThat(filterDictionaryDto.getName()).isEqualTo(filterDictionary.getName());
    }

    @Test
    void testNotExistingFilterDictionary() {
        assertThrows(EntityNotFoundException.class,
                () -> filterService.getFilterDictionary(DICTIONARY_NAME));
    }
}
