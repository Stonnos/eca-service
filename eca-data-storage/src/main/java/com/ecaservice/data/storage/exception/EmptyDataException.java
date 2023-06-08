package com.ecaservice.data.storage.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.data.storage.error.DsErrorCode;

/**
 * Exception throws in case if data set is empty.
 *
 * @author Roman Batygin
 */
public class EmptyDataException extends ValidationErrorException {

    /**
     * Creates exception object.
     */
    public EmptyDataException() {
        super(DsErrorCode.EMPTY_DATA_SET, "Expected not empty data set");
    }
}
