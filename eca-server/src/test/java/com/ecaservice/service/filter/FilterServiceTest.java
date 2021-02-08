package com.ecaservice.service.filter;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.mapping.filters.FilterDictionaryMapperImpl;
import com.ecaservice.mapping.filters.FilterDictionaryValueMapperImpl;
import com.ecaservice.mapping.filters.FilterFieldMapperImpl;
import com.ecaservice.model.entity.FilterDictionary;
import com.ecaservice.model.entity.FilterTemplate;
import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.model.entity.GlobalFilterTemplate;
import com.ecaservice.repository.FilterDictionaryRepository;
import com.ecaservice.repository.FilterTemplateRepository;
import com.ecaservice.repository.GlobalFilterTemplateRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.filter.dictionary.FilterDictionaries;
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
        FilterTemplate filterTemplate = TestHelperUtils.createFilterTemplate(FilterTemplateType.EVALUATION_LOG);
        filterTemplateRepository.save(filterTemplate);
        List<FilterFieldDto> filterFieldDtoList = filterService.getFilterFields(FilterTemplateType.EVALUATION_LOG);
        assertThat(filterFieldDtoList).hasSameSizeAs(filterTemplate.getFields());
    }

    @Test
    void testNotExistingFilterTemplate() {
        assertThrows(EntityNotFoundException.class,
                () -> filterService.getFilterFields(FilterTemplateType.EVALUATION_LOG));
    }

    @Test
    void testGetGlobalFilterTemplateFields() {
        GlobalFilterTemplate filterTemplate =
                TestHelperUtils.createGlobalFilterTemplate(FilterTemplateType.EVALUATION_LOG);
        globalFilterTemplateRepository.save(filterTemplate);
        List<String> fields = filterService.getGlobalFilterFields(FilterTemplateType.EVALUATION_LOG);
        assertThat(fields).hasSameSizeAs(filterTemplate.getFields());
    }

    @Test
    void testNotExistingGlobalFilterTemplate() {
        assertThrows(EntityNotFoundException.class,
                () -> filterService.getGlobalFilterFields(FilterTemplateType.EVALUATION_LOG));
    }

    @Test
    void testGetFilterDictionary() {
        FilterDictionary filterDictionary = new FilterDictionary();
        filterDictionary.setName(FilterDictionaries.EVALUATION_METHOD);
        filterDictionaryRepository.save(filterDictionary);
        FilterDictionaryDto filterDictionaryDto =
                filterService.getFilterDictionary(FilterDictionaries.EVALUATION_METHOD);
        assertThat(filterDictionaryDto).isNotNull();
        assertThat(filterDictionaryDto.getName()).isEqualTo(filterDictionary.getName());
    }

    @Test
    void testNotExistingFilterDictionary() {
        assertThrows(EntityNotFoundException.class,
                () -> filterService.getFilterDictionary(FilterDictionaries.EVALUATION_METHOD));
    }
}
