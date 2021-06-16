package com.ecaservice.filter;

import com.ecaservice.core.filter.AbstractFilter;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.web.dto.model.FilterRequestDto;

import java.util.List;

/**
 * Implements experiment filter.
 *
 * @author Roman Batygin
 */
public class ExperimentFilter extends AbstractFilter<Experiment> {

    /**
     * Constructor with filters requests.
     *
     * @param globalFilterFields - global filter fields list
     * @param searchQuery        - search query string
     * @param filters            - filters requests list
     */
    public ExperimentFilter(String searchQuery, List<String> globalFilterFields, List<FilterRequestDto> filters) {
        super(Experiment.class, searchQuery, globalFilterFields, filters);
    }
}
