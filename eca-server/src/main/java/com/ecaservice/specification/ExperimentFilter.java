package com.ecaservice.specification;

import com.ecaservice.model.entity.Experiment;
import com.ecaservice.web.dto.FilterRequestDto;

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
     * @param filters - filters requests list
     */
    public ExperimentFilter(List<FilterRequestDto> filters) {
        super(Experiment.class, filters);
    }
}
