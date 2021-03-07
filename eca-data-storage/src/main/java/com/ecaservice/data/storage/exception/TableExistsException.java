package com.ecaservice.data.storage.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;

/**
 * Exception throws in case if table name is exists.
 *
 * @author Roman Batygin
 */
public class TableExistsException extends ValidationErrorException {

    /**
     * Creates exception object.
     *
     * @param tableName - table name
     */
    public TableExistsException(String tableName) {
        super("UniqueTableName", String.format("Table with name [%s] already exists!", tableName));
    }
}
