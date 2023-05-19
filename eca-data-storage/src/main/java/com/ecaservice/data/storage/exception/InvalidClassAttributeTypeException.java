package com.ecaservice.data.storage.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;

/**
 * Exception throws in case if class attribute type is invalid.
 *
 * @author Roman Batygin
 */
public class InvalidClassAttributeTypeException extends ValidationErrorException {

    private static final String ERROR_CODE = "InvalidClassAttributeType";

    /**
     * Creates exception object.
     *
     * @param attributeName - attribute name
     */
    public InvalidClassAttributeTypeException(String attributeName) {
        super(ERROR_CODE, String.format("Class attribute [%s] must be nominal", attributeName));
    }
}
