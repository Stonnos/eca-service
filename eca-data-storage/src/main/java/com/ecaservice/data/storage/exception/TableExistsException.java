package com.ecaservice.data.storage.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.data.storage.error.DsErrorCode;

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
        super(DsErrorCode.TABLE_NAME_DUPLICATION, String.format("Table with name [%s] already exists!", tableName));
    }
}
