package com.ecaservice.core.filter.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;

/**
 * Field not found exception.
 *
 * @author Roman Batygin
 */
public class FieldNotFoundException extends ValidationErrorException {

    private static final String ERROR_CODE = "FieldNotFound";

    /**
     * Constructor with parameters.
     *
     * @param fieldName - field name
     */
    public FieldNotFoundException(String fieldName) {
        super(ERROR_CODE, String.format("Field [%s] doesn't exists", fieldName));
    }
}
