package com.ecaservice.core.filter.util;

import com.ecaservice.web.dto.model.FilterDictionaryDto;
import com.ecaservice.web.dto.model.FilterDictionaryValueDto;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Filter template utils.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class FilterTemplateUtils {

    /**
     * Finds filter dictionary values (codes) by label.
     *
     * @param filterDictionaryDto - filter dictionary dto
     * @param searchQuery         - search query string
     * @return filter dictionary values
     */
    public static List<String> findValuesByLabel(FilterDictionaryDto filterDictionaryDto, String searchQuery) {
        return filterDictionaryDto.getValues()
                .stream()
                .filter(filterDictionaryValueDto -> filterDictionaryValueDto.getLabel().toLowerCase().contains(
                        searchQuery.toLowerCase()))
                .map(FilterDictionaryValueDto::getValue)
                .collect(Collectors.toList());
    }
}
