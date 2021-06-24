package com.ecaservice.audit.filter;

import com.ecaservice.audit.entity.AuditLogEntity;
import com.ecaservice.core.filter.specification.AbstractFilter;
import com.ecaservice.web.dto.model.FilterRequestDto;

import java.util.List;

/**
 * Implements audit log filter.
 *
 * @author Roman Batygin
 */
public class AuditLogFilter extends AbstractFilter<AuditLogEntity> {

    /**
     * Constructor with filters requests.
     *
     * @param globalFilterFields - global filter fields list
     * @param searchQuery        - search query string
     * @param filters            - filters requests list
     */
    public AuditLogFilter(String searchQuery, List<String> globalFilterFields, List<FilterRequestDto> filters) {
        super(AuditLogEntity.class, searchQuery, globalFilterFields, filters);
    }
}
