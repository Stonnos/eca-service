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
import com.ecaservice.core.filter.repository.SortTemplateRepository;
import com.ecaservice.web.dto.model.FilterDictionaryDto;
import com.ecaservice.web.dto.model.FilterFieldDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for checking {@link FilterTemplateService} functionality.
 *
 * @author Roman Batygin
 */
@Import({FilterFieldMapperImpl.class, FilterDictionaryMapperImpl.class, FilterDictionaryValueMapperImpl.class,
        FilterTemplateService.class})
class FilterTemplateServiceTest extends AbstractJpaTest {

    public static final String FILTER_TEMPLATE_TYPE = "template";
    public static final String DICTIONARY_NAME = "dictionaryName";
    @Autowired
    private FilterTemplateRepository filterTemplateRepository;
    @Autowired
    private GlobalFilterTemplateRepository globalFilterTemplateRepository;
    @Autowired
    private SortTemplateRepository sortTemplateRepository;
    @Autowired
    private FilterDictionaryRepository filterDictionaryRepository;

    @Autowired
    private FilterTemplateService filterTemplateService;

    @Override
    public void deleteAll() {
        filterTemplateRepository.deleteAll();
        globalFilterTemplateRepository.deleteAll();
        filterDictionaryRepository.deleteAll();
        sortTemplateRepository.deleteAll();
    }

    @Test
    void testGetFilterTemplateFields() {
        FilterTemplate filterTemplate = TestHelperUtils.createFilterTemplate(FILTER_TEMPLATE_TYPE);
        filterTemplateRepository.save(filterTemplate);
        List<FilterFieldDto> filterFieldDtoList = filterTemplateService.getFilterFields(FILTER_TEMPLATE_TYPE);
        assertThat(filterFieldDtoList).hasSameSizeAs(filterTemplate.getFields());
    }

    @Test
    void testNotExistingFilterTemplate() {
        assertThrows(EntityNotFoundException.class,
                () -> filterTemplateService.getFilterFields(FILTER_TEMPLATE_TYPE));
    }

    @Test
    void testGetGlobalFilterTemplateFields() {
        GlobalFilterTemplate filterTemplate =
                TestHelperUtils.createGlobalFilterTemplate(FILTER_TEMPLATE_TYPE);
        globalFilterTemplateRepository.save(filterTemplate);
        List<String> fields = filterTemplateService.getGlobalFilterFields(FILTER_TEMPLATE_TYPE);
        assertThat(fields).hasSameSizeAs(filterTemplate.getFields());
    }

    @Test
    void testNotExistingGlobalFilterTemplate() {
        assertThrows(EntityNotFoundException.class,
                () -> filterTemplateService.getGlobalFilterFields(FILTER_TEMPLATE_TYPE));
    }

    @Test
    void testGetFilterDictionary() {
        FilterDictionary filterDictionary = new FilterDictionary();
        filterDictionary.setName(DICTIONARY_NAME);
        filterDictionaryRepository.save(filterDictionary);
        FilterDictionaryDto filterDictionaryDto =
                filterTemplateService.getFilterDictionary(DICTIONARY_NAME);
        assertThat(filterDictionaryDto).isNotNull();
        assertThat(filterDictionaryDto.getName()).isEqualTo(filterDictionary.getName());
    }

    @Test
    void testNotExistingFilterDictionary() {
        assertThrows(EntityNotFoundException.class,
                () -> filterTemplateService.getFilterDictionary(DICTIONARY_NAME));
    }

    @Test
    void testGetSortFilterTemplateFields() {
        var sortTemplate = TestHelperUtils.createSortTemplate(FILTER_TEMPLATE_TYPE);
        sortTemplateRepository.save(sortTemplate);
        List<String> fields = filterTemplateService.getSortFields(FILTER_TEMPLATE_TYPE);
        assertThat(fields).hasSameSizeAs(sortTemplate.getSortFields());
    }

    @Test
    void testNotExistingSortTemplate() {
        assertThrows(EntityNotFoundException.class,
                () -> filterTemplateService.getFilterFields(FILTER_TEMPLATE_TYPE));
    }
}
