package com.ecaservice.data.storage.filter;

import com.ecaservice.core.filter.specification.AbstractFilter;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.web.dto.model.FilterRequestDto;

import java.util.List;

/**
 * Implements instances filter.
 *
 * @author Roman Batygin
 */
public class InstancesFilter extends AbstractFilter<InstancesEntity> {

    /**
     * Constructor with filters requests.
     *
     * @param globalFilterFields - global filter fields list
     * @param searchQuery        - search query string
     * @param filters            - filters requests list
     */
    public InstancesFilter(String searchQuery, List<String> globalFilterFields, List<FilterRequestDto> filters) {
        super(InstancesEntity.class, searchQuery, globalFilterFields, filters);
    }
}
