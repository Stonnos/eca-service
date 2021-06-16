package com.ecaservice.core.filter.mapping;

import com.ecaservice.core.filter.TestHelperUtils;
import com.ecaservice.core.filter.entity.FilterDictionaryValue;
import com.ecaservice.web.dto.model.FilterDictionaryValueDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

/**
 * Unit test for checking {@link FilterDictionaryValueMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(FilterDictionaryValueMapperImpl.class)
class FilterDictionaryValueMapperTest {

    @Inject
    private FilterDictionaryValueMapper filterDictionaryValueMapper;

    @Test
    void testMapFilterDictionaryValue() {
        FilterDictionaryValue filterDictionaryValue = TestHelperUtils.createFilterDictionaryValue();
        FilterDictionaryValueDto filterDictionaryValueDto = filterDictionaryValueMapper.map(filterDictionaryValue);
        Assertions.assertThat(filterDictionaryValueDto).isNotNull();
        Assertions.assertThat(filterDictionaryValueDto.getLabel()).isEqualTo(filterDictionaryValue.getLabel());
        Assertions.assertThat(filterDictionaryValueDto.getValue()).isEqualTo(filterDictionaryValue.getValue());
    }
}
