package com.ecaservice.data.storage.model;

import eca.data.db.SqlQueryHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import weka.core.Instances;

/**
 * Instances batch options.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
public class InstancesBatchOptions {

    /**
     * Table name
     */
    public String tableName;

    /**
     * Instances object
     */
    private Instances instances;

    /**
     * Batch size
     */
    private int limit;

    /**
     * Offset value
     */
    private int offset;

    /**
     * Sql query helper
     */
    private SqlQueryHelper sqlQueryHelper;
}
