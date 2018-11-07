package com.ecaservice.filter;

import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.web.dto.FilterRequestDto;

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
     * @param filters - filters requests list
     */
    public ClassifierOptionsRequestModelFilter(List<FilterRequestDto> filters) {
        super(ClassifierOptionsRequestModel.class, filters);
    }
}
