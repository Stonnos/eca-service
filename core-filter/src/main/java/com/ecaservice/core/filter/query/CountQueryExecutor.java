package com.ecaservice.core.filter.query;

import com.ecaservice.core.filter.specification.AbstractFilter;
import com.ecaservice.web.dto.model.PageRequestDto;

/**
 * Count query executor.
 *
 * @author Roman Batygin
 */
public interface CountQueryExecutor {

    /**
     * Executes count query.
     *
     * @param filter         - filter object
     * @param pageRequestDto - page request dto
     * @param <T>            entity generic type
     * @return rows count
     */
    <T> long countQuery(AbstractFilter<T> filter, PageRequestDto pageRequestDto);
}
