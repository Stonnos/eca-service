package com.ecaservice.mapping.filters;

import com.ecaservice.model.entity.FilterDictionary;
import com.ecaservice.model.entity.FilterField;
import com.ecaservice.web.dto.model.FilterFieldDto;
import com.ecaservice.web.dto.model.FilterFieldType;
import com.ecaservice.web.dto.model.MatchMode;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

/**
 * Unit test for checking {@link FilterFieldMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import({FilterDictionaryValueMapperImpl.class, FilterDictionaryMapperImpl.class, FilterFieldMapperImpl.class})
public class FilterFieldMapperTest {

    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";

    @Inject
    private FilterFieldMapper filterFieldMapper;

    @Test
    public void testMapFilterField() {
        FilterField filterField = new FilterField();
        filterField.setDescription(DESCRIPTION);
        filterField.setFieldOrder(1);
        filterField.setName(NAME);
        filterField.setFilterFieldType(FilterFieldType.REFERENCE);
        filterField.setMatchMode(MatchMode.EQUALS);
        filterField.setDictionary(new FilterDictionary());
        FilterFieldDto filterFieldDto = filterFieldMapper.map(filterField);
        Assertions.assertThat(filterFieldDto).isNotNull();
        Assertions.assertThat(filterFieldDto.getDescription()).isEqualTo(filterField.getDescription());
        Assertions.assertThat(filterFieldDto.getName()).isEqualTo(filterField.getName());
        Assertions.assertThat(filterFieldDto.getFieldOrder()).isEqualTo(filterField.getFieldOrder());
        Assertions.assertThat(filterFieldDto.getFilterFieldType()).isEqualTo(filterField.getFilterFieldType());
        Assertions.assertThat(filterFieldDto.getMatchMode()).isEqualTo(filterField.getMatchMode());
        Assertions.assertThat(filterFieldDto.getDictionary()).isNotNull();
    }

    @Test
    public void testMapFilterFieldsList() {
        FilterField filterField = new FilterField();
        filterField.setFilterFieldType(FilterFieldType.REFERENCE);
        filterField.setMatchMode(MatchMode.EQUALS);
        filterField.setDictionary(new FilterDictionary());
        FilterField filterField1 = new FilterField();
        filterField1.setFilterFieldType(FilterFieldType.REFERENCE);
        filterField1.setMatchMode(MatchMode.LIKE);
        filterField1.setDictionary(new FilterDictionary());
        List<FilterFieldDto> filterFieldDtoList = filterFieldMapper.map(Arrays.asList(filterField, filterField1));
        Assertions.assertThat(filterFieldDtoList).isNotNull();
        Assertions.assertThat(filterFieldDtoList).hasSize(2);
    }
}
