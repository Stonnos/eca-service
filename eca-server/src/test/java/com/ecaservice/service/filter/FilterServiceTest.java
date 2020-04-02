package com.ecaservice.service.filter;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.exception.EntityNotFoundException;
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
import org.junit.Test;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link FilterService} functionality.
 *
 * @author Roman Batygin
 */
@Import({FilterFieldMapperImpl.class, FilterDictionaryMapperImpl.class, FilterDictionaryValueMapperImpl.class,
        FilterService.class})
public class FilterServiceTest extends AbstractJpaTest {

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
    public void testGetFilterTemplateFields() {
        FilterTemplate filterTemplate = TestHelperUtils.createFilterTemplate(FilterTemplateType.EVALUATION_LOG);
        filterTemplateRepository.save(filterTemplate);
        List<FilterFieldDto> filterFieldDtoList = filterService.getFilterFields(FilterTemplateType.EVALUATION_LOG);
        assertThat(filterFieldDtoList).hasSameSizeAs(filterTemplate.getFields());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testNotExistingFilterTemplate() {
        filterService.getFilterFields(FilterTemplateType.EVALUATION_LOG);
    }

    @Test
    public void testGetGlobalFilterTemplateFields() {
        GlobalFilterTemplate filterTemplate =
                TestHelperUtils.createGlobalFilterTemplate(FilterTemplateType.EVALUATION_LOG);
        globalFilterTemplateRepository.save(filterTemplate);
        List<String> fields = filterService.getGlobalFilterFields(FilterTemplateType.EVALUATION_LOG);
        assertThat(fields).hasSameSizeAs(filterTemplate.getFields());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testNotExistingGlobalFilterTemplate() {
        filterService.getGlobalFilterFields(FilterTemplateType.EVALUATION_LOG);
    }

    @Test
    public void testGetFilterDictionary() {
        FilterDictionary filterDictionary = new FilterDictionary();
        filterDictionary.setName(FilterDictionaries.EVALUATION_METHOD);
        filterDictionaryRepository.save(filterDictionary);
        FilterDictionaryDto filterDictionaryDto =
                filterService.getFilterDictionary(FilterDictionaries.EVALUATION_METHOD);
        assertThat(filterDictionaryDto).isNotNull();
        assertThat(filterDictionaryDto.getName()).isEqualTo(filterDictionary.getName());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testNotExistingFilterDictionary() {
        filterService.getFilterDictionary(FilterDictionaries.EVALUATION_METHOD);
    }
}
