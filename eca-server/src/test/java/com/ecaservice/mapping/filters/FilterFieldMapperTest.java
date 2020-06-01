package com.ecaservice.mapping.filters;

import com.ecaservice.model.entity.FilterDictionary;
import com.ecaservice.model.entity.FilterField;
import com.ecaservice.web.dto.model.FilterFieldDto;
import com.ecaservice.web.dto.model.FilterFieldType;
import com.ecaservice.web.dto.model.MatchMode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

/**
 * Unit test for checking {@link FilterFieldMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({FilterDictionaryValueMapperImpl.class, FilterDictionaryMapperImpl.class, FilterFieldMapperImpl.class})
class FilterFieldMapperTest {

    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";

    @Inject
    private FilterFieldMapper filterFieldMapper;

    @Test
    void testMapFilterField() {
        FilterField filterField = new FilterField();
        filterField.setDescription(DESCRIPTION);
        filterField.setFieldOrder(1);
        filterField.setFieldName(NAME);
        filterField.setFilterFieldType(FilterFieldType.REFERENCE);
        filterField.setMatchMode(MatchMode.EQUALS);
        filterField.setMultiple(true);
        filterField.setDictionary(new FilterDictionary());
        FilterFieldDto filterFieldDto = filterFieldMapper.map(filterField);
        Assertions.assertThat(filterFieldDto).isNotNull();
        Assertions.assertThat(filterFieldDto.getDescription()).isEqualTo(filterField.getDescription());
        Assertions.assertThat(filterFieldDto.getFieldName()).isEqualTo(filterField.getFieldName());
        Assertions.assertThat(filterFieldDto.getFieldOrder()).isEqualTo(filterField.getFieldOrder());
        Assertions.assertThat(filterFieldDto.getFilterFieldType()).isEqualTo(filterField.getFilterFieldType());
        Assertions.assertThat(filterFieldDto.getMatchMode()).isEqualTo(filterField.getMatchMode());
        Assertions.assertThat(filterFieldDto.isMultiple()).isEqualTo(filterField.isMultiple());
        Assertions.assertThat(filterFieldDto.getDictionary()).isNotNull();
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
        List<FilterFieldDto> filterFieldDtoList = filterFieldMapper.map(filterFields);
        Assertions.assertThat(filterFieldDtoList).hasSameSizeAs(filterFields);
    }
}
