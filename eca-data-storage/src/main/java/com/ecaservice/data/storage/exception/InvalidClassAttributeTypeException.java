package com.ecaservice.data.storage.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.data.storage.error.DsErrorCode;

/**
 * Exception throws in case if class attribute type is invalid.
 *
 * @author Roman Batygin
 */
public class InvalidClassAttributeTypeException extends ValidationErrorException {

    /**
     * Creates exception object.
     *
     * @param id - attribute id
     */
    public InvalidClassAttributeTypeException(long id) {
        super(DsErrorCode.INVALID_CLASS_ATTRIBUTE_TYPE,
                String.format("Class attribute [%d] must be nominal", id));
    }
}
