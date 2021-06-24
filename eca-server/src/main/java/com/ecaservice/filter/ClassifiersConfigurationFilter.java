package com.ecaservice.filter;

import com.ecaservice.core.filter.specification.AbstractFilter;
import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.web.dto.model.FilterRequestDto;

import java.util.List;

/**
 * Implements classifiers configuration filter.
 *
 * @author Roman Batygin
 */
public class ClassifiersConfigurationFilter extends AbstractFilter<ClassifiersConfiguration> {

    /**
     * Constructor with filters requests.
     *
     * @param globalFilterFields - global filter fields list
     * @param searchQuery        - search query string
     * @param filters            - filters requests list
     */
    public ClassifiersConfigurationFilter(String searchQuery, List<String> globalFilterFields, List<FilterRequestDto> filters) {
        super(ClassifiersConfiguration.class, searchQuery, globalFilterFields, filters);
    }
}
