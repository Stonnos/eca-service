package com.ecaservice.data.storage.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;

import static com.ecaservice.data.storage.util.Utils.MIN_NUM_CLASSES;

/**
 * Exception throws in case if class attribute has invalid values.
 *
 * @author Roman Batygin
 */
public class ClassAttributeValuesOutOfBoundsException extends ValidationErrorException {

    private static final String ERROR_CODE = "ClassAttributeValuesOutOfBounds";

    /**
     * Creates exception object.
     *
     * @param id - attribute id
     */
    public ClassAttributeValuesOutOfBoundsException(long id) {
        super(ERROR_CODE, String.format("Class attribute id [%s] must have greater than or equal to [%d] values",
                id, MIN_NUM_CLASSES));
    }
}
