package com.ecaservice.mapping.filters;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.entity.FilterDictionary;
import com.ecaservice.web.dto.model.FilterDictionaryDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.Collections;

/**
 * Unit test for checking {@link FilterDictionaryMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({FilterDictionaryValueMapperImpl.class, FilterDictionaryMapperImpl.class})
public class FilterDictionaryMapperTest {

    private static final String NAME = "name";

    @Inject
    private FilterDictionaryMapper filterDictionaryMapper;

    @Test
    public void testMapFilterDictionary() {
        FilterDictionary filterDictionary = new FilterDictionary();
        filterDictionary.setName(NAME);
        filterDictionary.setValues(Collections.singletonList(TestHelperUtils.createFilterDictionaryValue()));
        FilterDictionaryDto filterDictionaryDto = filterDictionaryMapper.map(filterDictionary);
        Assertions.assertThat(filterDictionaryDto).isNotNull();
        Assertions.assertThat(filterDictionaryDto.getName()).isEqualTo(filterDictionary.getName());
        Assertions.assertThat(filterDictionaryDto.getValues()).isNotEmpty();
        Assertions.assertThat(filterDictionaryDto.getValues().size()).isOne();
    }
}
