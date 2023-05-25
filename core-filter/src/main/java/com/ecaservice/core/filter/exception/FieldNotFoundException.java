package com.ecaservice.core.filter.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.core.filter.error.CoreFilterErrorCode;

/**
 * Field not found exception.
 *
 * @author Roman Batygin
 */
public class FieldNotFoundException extends ValidationErrorException {

    /**
     * Constructor with parameters.
     *
     * @param fieldName - field name
     * @param clazz     - class type
     */
    public FieldNotFoundException(String fieldName, Class<?> clazz) {
        super(CoreFilterErrorCode.FIELD_NOT_FOUND,
                String.format("Field [%s] not found for type [%s]", fieldName, clazz.getSimpleName()));
    }

    /**
     * Constructor with parameters.
     *
     * @param errorMessage - error message
     */
    public FieldNotFoundException(String errorMessage) {
        super(CoreFilterErrorCode.FIELD_NOT_FOUND, errorMessage);
    }
}
