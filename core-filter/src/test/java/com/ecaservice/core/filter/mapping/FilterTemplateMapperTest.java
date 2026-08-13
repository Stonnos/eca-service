package com.ecaservice.core.filter.mapping;

import com.ecaservice.core.filter.TestHelperUtils;
import com.ecaservice.core.filter.model.FilterDictionary;
import com.ecaservice.core.filter.model.FilterDictionaryValue;
import com.ecaservice.core.filter.model.FilterField;
import com.ecaservice.web.dto.model.FilterDictionaryDto;
import com.ecaservice.web.dto.model.FilterDictionaryValueDto;
import com.ecaservice.web.dto.model.FilterFieldDto;
import com.ecaservice.web.dto.model.FilterFieldType;
import com.ecaservice.web.dto.model.MatchMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for checking {@link FilterTemplateMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(FilterTemplateMapperImpl.class)
class FilterTemplateMapperTest {

    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";

    @Autowired
    private FilterTemplateMapper filterTemplateMapper;

    @Test
    void testMapFilterField() {
        FilterField filterField = new FilterField();
        filterField.setDescription(DESCRIPTION);
        filterField.setFieldName(NAME);
        filterField.setFilterFieldType(FilterFieldType.REFERENCE);
        filterField.setMatchMode(MatchMode.EQUALS);
        filterField.setMultiple(true);
        filterField.setDictionary(new FilterDictionary());
        FilterFieldDto filterFieldDto = filterTemplateMapper.map(filterField);
        assertThat(filterFieldDto).isNotNull();
        assertThat(filterFieldDto.getDescription()).isEqualTo(filterField.getDescription());
        assertThat(filterFieldDto.getFieldName()).isEqualTo(filterField.getFieldName());
        assertThat(filterFieldDto.getFilterFieldType()).isEqualTo(filterField.getFilterFieldType());
        assertThat(filterFieldDto.getMatchMode()).isEqualTo(filterField.getMatchMode());
        assertThat(filterFieldDto.isMultiple()).isEqualTo(filterField.isMultiple());
        assertThat(filterFieldDto.getDictionary()).isNotNull();
    }

    @Test
    void testMapFilterFieldsList() {
        FilterField filterField = new FilterField();
        filterField.setFilterFieldType(FilterFieldType.REFERENCE);
        filterField.setMatchMode(MatchMode.EQUALS);
        filterField.setDictionary(new FilterDictionary());
        FilterField filterField1 = new FilterField();
        filterField1.setFilterFieldType(FilterFieldType.REFERENCE);
        filterField1.setMatchMode(MatchMode.LIKE);
        filterField1.setDictionary(new FilterDictionary());
        List<FilterField> filterFields = Arrays.asList(filterField, filterField1);
        List<FilterFieldDto> filterFieldDtoList = filterTemplateMapper.mapFields(filterFields);
        assertThat(filterFieldDtoList).hasSameSizeAs(filterFields);
    }

    @Test
    void testMapFilterDictionaryValue() {
        FilterDictionaryValue filterDictionaryValue = TestHelperUtils.createFilterDictionaryValue();
        FilterDictionaryValueDto filterDictionaryValueDto = filterTemplateMapper.map(filterDictionaryValue);
        assertThat(filterDictionaryValueDto).isNotNull();
        assertThat(filterDictionaryValueDto.getLabel()).isEqualTo(filterDictionaryValue.getLabel());
        assertThat(filterDictionaryValueDto.getValue()).isEqualTo(filterDictionaryValue.getValue());
    }

    @Test
    void testMapFilterDictionary() {
        FilterDictionary filterDictionary = new FilterDictionary();
        filterDictionary.setName(NAME);
        filterDictionary.setValues(Collections.singletonList(TestHelperUtils.createFilterDictionaryValue()));
        FilterDictionaryDto filterDictionaryDto = filterTemplateMapper.map(filterDictionary);
        assertThat(filterDictionaryDto).isNotNull();
        assertThat(filterDictionaryDto.getName()).isEqualTo(filterDictionary.getName());
        assertThat(filterDictionaryDto.getValues()).isNotEmpty();
        assertThat(filterDictionaryDto.getValues().size()).isOne();
    }
}
