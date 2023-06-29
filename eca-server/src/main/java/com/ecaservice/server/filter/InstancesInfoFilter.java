package com.ecaservice.server.filter;

import com.ecaservice.core.filter.specification.AbstractFilter;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.web.dto.model.FilterRequestDto;

import java.util.List;

/**
 * Instances info filter.
 *
 * @author Roman Batygin
 */
public class InstancesInfoFilter extends AbstractFilter<InstancesInfo> {

    /**
     * Constructor with filters requests.
     *
     * @param globalFilterFields - global filter fields list
     * @param searchQuery        - search query string
     * @param filters            - filters requests list
     */
    public InstancesInfoFilter(String searchQuery, List<String> globalFilterFields, List<FilterRequestDto> filters) {
        super(InstancesInfo.class, searchQuery, globalFilterFields, filters);
    }
}
