package com.ecaservice.server.filter;

import com.ecaservice.core.filter.specification.AbstractFilter;
import com.ecaservice.server.model.entity.ClassifiersConfigurationHistoryEntity;
import com.ecaservice.web.dto.model.FilterRequestDto;

import java.util.List;

/**
 * Implements classifiers configuration history filter.
 *
 * @author Roman Batygin
 */
public class ClassifiersConfigurationHistoryFilter extends AbstractFilter<ClassifiersConfigurationHistoryEntity> {

    /**
     * Constructor with filters requests.
     *
     * @param globalFilterFields - global filter fields list
     * @param searchQuery        - search query string
     * @param filters            - filters requests list
     */
    public ClassifiersConfigurationHistoryFilter(String searchQuery,
                                                 List<String> globalFilterFields,
                                                 List<FilterRequestDto> filters) {
        super(ClassifiersConfigurationHistoryEntity.class, searchQuery, globalFilterFields, filters);
    }
}
