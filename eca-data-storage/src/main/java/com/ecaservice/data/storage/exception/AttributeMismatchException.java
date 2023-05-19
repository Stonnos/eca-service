package com.ecaservice.data.storage.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;

/**
 * Exception throws in case if instances does not contain attribute.
 *
 * @author Roman Batygin
 */
public class AttributeMismatchException extends ValidationErrorException {

    private static final String ERROR_CODE = "AttributeMismatch";

    /**
     * Creates exception object.
     *
     * @param tableName   - table name
     * @param attributeId - attribute id
     */
    public AttributeMismatchException(String tableName, long attributeId) {
        super(ERROR_CODE, String.format("Instances [%s] does not contain attribute with id [%d]", tableName,
                attributeId));
    }
}
