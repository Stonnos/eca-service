package com.ecaservice.data.storage.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;

/**
 * Exception throws in case if table name is exists.
 *
 * @author Roman Batygin
 */
public class TableExistsException extends ValidationErrorException {

    private static final String ERROR_CODE = "UniqueTableName";

    /**
     * Creates exception object.
     *
     * @param tableName - table name
     */
    public TableExistsException(String tableName) {
        super(ERROR_CODE, String.format("Table with name [%s] already exists!", tableName));
    }
}
