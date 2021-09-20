package com.ecaservice.mail.filter;

import com.ecaservice.core.filter.specification.AbstractFilter;
import com.ecaservice.mail.model.TemplateEntity;
import com.ecaservice.web.dto.model.FilterRequestDto;

import java.util.List;

/**
 * Implements email template filter.
 *
 * @author Roman Batygin
 */
public class TemplateFilter extends AbstractFilter<TemplateEntity> {

    /**
     * Constructor with filters requests.
     *
     * @param globalFilterFields - global filter fields list
     * @param searchQuery        - search query string
     * @param filters            - filters requests list
     */
    public TemplateFilter(String searchQuery, List<String> globalFilterFields, List<FilterRequestDto> filters) {
        super(TemplateEntity.class, searchQuery, globalFilterFields, filters);
    }
}
