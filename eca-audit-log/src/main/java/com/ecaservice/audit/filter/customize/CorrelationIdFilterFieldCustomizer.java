package com.ecaservice.audit.filter.customize;

import com.ecaservice.audit.entity.AuditLogEntity_;
import com.ecaservice.core.filter.specification.FilterFieldCustomizer;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Uuid filter field customizer.
 *
 * @author Roman Batygin
 */
public class CorrelationIdFilterFieldCustomizer extends FilterFieldCustomizer {

    /**
     * Constructor with spring dependency injection.
     */
    public CorrelationIdFilterFieldCustomizer() {
        super(AuditLogEntity_.CORRELATION_ID);
    }

    @Override
    public Predicate toPredicate(Root<?> root, CriteriaBuilder criteriaBuilder, String value) {
        return criteriaBuilder.equal(root.get(getFieldName()), value);
    }
}
