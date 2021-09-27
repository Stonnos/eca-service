package com.ecaservice.data.storage.model;

import lombok.Builder;
import lombok.Data;

/**
 * Database column model.
 * @author Roman Batygin
 */
@Data
@Builder
public class ColumnModel {

    /**
     * Column name
     */
    private String columnName;

    /**
     * Column data type
     */
    private String dataType;
}
