package com.ecaservice.data.storage.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.data.storage.dto.DsInternalApiErrorCode;

/**
 * Exception throws in case if class attribute type not selected.
 *
 * @author Roman Batygin
 */
public class ClassAttributeNotSelectedException extends ValidationErrorException {

    /**
     * Creates exception object.
     *
     * @param id - attribute id
     */
    public ClassAttributeNotSelectedException(long id) {
        super(DsInternalApiErrorCode.CLASS_ATTRIBUTE_NOT_SELECTED,
                String.format("Class attribute not selected for instances [%d]", id));
    }
}
