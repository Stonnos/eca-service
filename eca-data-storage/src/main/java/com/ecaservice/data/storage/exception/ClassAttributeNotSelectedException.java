package com.ecaservice.data.storage.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;

/**
 * Exception throws in case if class attribute type not selected.
 *
 * @author Roman Batygin
 */
public class ClassAttributeNotSelectedException extends ValidationErrorException {

    private static final String ERROR_CODE = "ClassAttributeNotSelected";

    /**
     * Creates exception object.
     *
     * @param id - attribute id
     */
    public ClassAttributeNotSelectedException(long id) {
        super(ERROR_CODE, String.format("Class attribute not selected for instances [%d]", id));
    }
}
