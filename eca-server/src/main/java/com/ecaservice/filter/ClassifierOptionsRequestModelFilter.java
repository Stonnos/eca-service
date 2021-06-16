package com.ecaservice.filter;

import com.ecaservice.core.filter.AbstractFilter;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.web.dto.model.FilterRequestDto;

import java.util.List;

/**
 * Implements classifier options request model filter.
 *
 * @author Roman Batygin
 */
public class ClassifierOptionsRequestModelFilter extends AbstractFilter<ClassifierOptionsRequestModel> {

    /**
     * Constructor with filters requests.
     *
     * @param globalFilterFields - global filter fields list
     * @param searchQuery        - search query string
     * @param filters            - filters requests list
     */
    public ClassifierOptionsRequestModelFilter(String searchQuery, List<String> globalFilterFields,
                                               List<FilterRequestDto> filters) {
        super(ClassifierOptionsRequestModel.class, searchQuery, globalFilterFields, filters);
    }
}
