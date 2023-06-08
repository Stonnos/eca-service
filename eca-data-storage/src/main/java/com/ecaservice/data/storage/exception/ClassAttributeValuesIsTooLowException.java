package com.ecaservice.data.storage.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.data.storage.dto.DsInternalApiErrorCode;

import static com.ecaservice.data.storage.util.Utils.MIN_NUM_CLASSES;

/**
 * Exception throws in case if class attribute has low values.
 *
 * @author Roman Batygin
 */
public class ClassAttributeValuesIsTooLowException extends ValidationErrorException {

    /**
     * Creates exception object.
     *
     * @param id - attribute id
     */
    public ClassAttributeValuesIsTooLowException(long id) {
        super(DsInternalApiErrorCode.CLASS_VALUES_IS_TOO_LOW,
                String.format("Class attribute id [%d] must have greater than or equal to [%d] values", id,
                        MIN_NUM_CLASSES));
    }
}
