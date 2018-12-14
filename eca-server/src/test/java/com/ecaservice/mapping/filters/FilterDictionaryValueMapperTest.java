package com.ecaservice.mapping.filters;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.entity.FilterDictionaryValue;
import com.ecaservice.web.dto.model.FilterDictionaryValueDto;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Unit test for checking {@link FilterDictionaryValueMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(FilterDictionaryValueMapperImpl.class)
public class FilterDictionaryValueMapperTest {

    @Inject
    private FilterDictionaryValueMapper filterDictionaryValueMapper;

    @Test
    public void testMapFilterDictionaryValue() {
        FilterDictionaryValue filterDictionaryValue = TestHelperUtils.createFilterDictionaryValue();
        FilterDictionaryValueDto filterDictionaryValueDto = filterDictionaryValueMapper.map(filterDictionaryValue);
        Assertions.assertThat(filterDictionaryValueDto).isNotNull();
        Assertions.assertThat(filterDictionaryValueDto.getLabel()).isEqualTo(filterDictionaryValue.getLabel());
        Assertions.assertThat(filterDictionaryValueDto.getValue()).isEqualTo(filterDictionaryValue.getValue());
    }
}
