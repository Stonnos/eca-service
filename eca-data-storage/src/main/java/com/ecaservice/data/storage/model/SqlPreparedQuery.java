package com.ecaservice.data.storage.model;

import lombok.Builder;
import lombok.Data;

/**
 * Sql prepared query model.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class SqlPreparedQuery {

    /**
     * Sql query string
     */
    private String query;

    /**
     * Sql count query
     */
    private String countQuery;

    /**
     * Query args
     */
    private Object[] args;
}
