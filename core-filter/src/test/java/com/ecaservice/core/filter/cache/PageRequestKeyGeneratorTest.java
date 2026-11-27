package com.ecaservice.core.filter.cache;

import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link PageRequestKeyGenerator} class.
 *
 * @author Roman Batygin
 */
class PageRequestKeyGeneratorTest {

    private static final String SEARCH_QUERY = "test-query";
    private static final String FILTER_NAME = "status";
    private static final String FILTER_VALUE = "ACTIVE";
    private static final String OTHER_PARAM = "other-param";
    private static final String EXPECTED_EMPTY_KEY = ":";
    private static final String EXPECTED_SEARCH_ONLY_KEY = ":test-query";
    private static final String EXPECTED_FILTERS_ONLY_KEY =
            "::[FilterRequestDto(name=status, values=[ACTIVE], matchMode=EQUALS)]";
    private static final String EXPECTED_FULL_KEY =
            ":test-query:[FilterRequestDto(name=status, values=[ACTIVE], matchMode=EQUALS)]";
    private static final String EXPECTED_ERROR_MESSAGE =
            "Expected only one argument of type [PageRequestDto] to generate cache key";

    private PageRequestKeyGenerator keyGenerator;
    private Method dummyMethod;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        keyGenerator = new PageRequestKeyGenerator();
        dummyMethod = Object.class.getMethod("toString");
    }

    @Test
    void testGenerateWhenNoPageRequestDtoProvided() {
        assertThatThrownBy(() -> keyGenerator.generate(new Object(), dummyMethod, OTHER_PARAM))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(EXPECTED_ERROR_MESSAGE);
    }

    @Test
    void testGenerateWhenMultiplePageRequestDtosProvided() {
        PageRequestDto firstRequest = new PageRequestDto();
        PageRequestDto secondRequest = new PageRequestDto();

        assertThatThrownBy(() -> keyGenerator.generate(new Object(), dummyMethod, firstRequest, secondRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(EXPECTED_ERROR_MESSAGE);
    }

    @Test
    void testGenerateWithEmptySearchQueryAndEmptyFilters() {
        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setSearchQuery("");
        pageRequestDto.setFilters(Collections.emptyList());
        Object key = keyGenerator.generate(new Object(), dummyMethod, OTHER_PARAM, pageRequestDto);
        assertThat(key).isEqualTo(EXPECTED_EMPTY_KEY);
    }

    @Test
    void testGenerateWithSearchQueryOnly() {
        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setSearchQuery(SEARCH_QUERY);
        Object key = keyGenerator.generate(new Object(), dummyMethod, pageRequestDto);
        assertThat(key).isEqualTo(EXPECTED_SEARCH_ONLY_KEY);
    }

    @Test
    void testGenerateWithFiltersOnly() {
        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setFilters(List.of(createFilter()));
        Object key = keyGenerator.generate(new Object(), dummyMethod, pageRequestDto);
        assertThat(key).isEqualTo(EXPECTED_FILTERS_ONLY_KEY);
    }

    @Test
    void testGenerateWithSearchQueryAndFilters() {
        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setSearchQuery(SEARCH_QUERY);
        pageRequestDto.setFilters(List.of(createFilter()));
        Object key = keyGenerator.generate(new Object(), dummyMethod, OTHER_PARAM, pageRequestDto);
        assertThat(key).isEqualTo(EXPECTED_FULL_KEY);
    }

    private FilterRequestDto createFilter() {
        return new FilterRequestDto(FILTER_NAME, List.of(FILTER_VALUE), MatchMode.EQUALS);
    }
}