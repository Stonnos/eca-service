package com.ecaservice.core.filter.service;

import com.ecaservice.core.filter.config.CoreFilterConfiguration;
import com.ecaservice.core.filter.mapping.FilterTemplateMapperImpl;
import com.ecaservice.web.dto.model.FilterDictionaryDto;
import com.ecaservice.web.dto.model.FilterFieldDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for checking {@link FilterTemplateService} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({FilterTemplateMapperImpl.class, FilterTemplateService.class, CoreFilterConfiguration.class})
class FilterTemplateServiceTest {

    private static final String FILTER_TEMPLATE_TYPE = "EVALUATION_RESULTS_HISTORY_TEMPLATE";
    private static final String DICTIONARY_NAME = "classifier";
    private static final int EXPECTED_FIELDS_SIZE = 4;
    private static final int EXPECTED_GLOBAL_SIZE = 3;
    private static final String INVALID_TEMPLATE = "invalidTemplate";
    private static final int EXPECTED_DICTIONARY_SIZE = 15;
    private static final int EXPECTED_SORT_FIELDS_SIZE = 8;
    private static final String INVALID_DICTIONARY = "ivalidDictionary";

    @Autowired
    private FilterTemplateService filterTemplateService;

    @Test
    void testGetFilterTemplateFields() {
        List<FilterFieldDto> filterFields = filterTemplateService.getFilterFields(FILTER_TEMPLATE_TYPE);
        assertThat(filterFields).hasSize(EXPECTED_FIELDS_SIZE);
    }

    @Test
    void testNotExistingFilterTemplate() {
        assertThrows(IllegalStateException.class,
                () -> filterTemplateService.getFilterFields(INVALID_TEMPLATE));
    }

    @Test
    void testGetGlobalFilterTemplateFields() {
        List<String> fields = filterTemplateService.getGlobalFilterFields(FILTER_TEMPLATE_TYPE);
        assertThat(fields).hasSize(EXPECTED_GLOBAL_SIZE);
    }

    @Test
    void testNotExistingGlobalFilterTemplate() {
        assertThrows(IllegalStateException.class, () -> filterTemplateService.getGlobalFilterFields(INVALID_TEMPLATE));
    }

    @Test
    void testGetFilterDictionary() {
        FilterDictionaryDto filterDictionaryDto = filterTemplateService.getFilterDictionary(DICTIONARY_NAME);
        assertThat(filterDictionaryDto).isNotNull();
        assertThat(filterDictionaryDto.getValues()).hasSize(EXPECTED_DICTIONARY_SIZE);
    }

    @Test
    void testNotExistingFilterDictionary() {
        assertThrows(IllegalStateException.class, () -> filterTemplateService.getFilterDictionary(INVALID_DICTIONARY));
    }

    @Test
    void testGetSortFilterTemplateFields() {
        List<String> fields = filterTemplateService.getSortFields(FILTER_TEMPLATE_TYPE);
        assertThat(fields).hasSize(EXPECTED_SORT_FIELDS_SIZE);
    }

    @Test
    void testNotExistingSortTemplate() {
        assertThrows(IllegalStateException.class, () -> filterTemplateService.getFilterFields(INVALID_TEMPLATE));
    }
}
