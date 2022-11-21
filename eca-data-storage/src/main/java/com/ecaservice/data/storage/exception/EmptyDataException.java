package com.ecaservice.data.storage.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;

/**
 * Exception throws in case if data set is empty.
 *
 * @author Roman Batygin
 */
public class EmptyDataException extends ValidationErrorException {

    private static final String ERROR_CODE = "EmptyDataSet";

    /**
     * Creates exception object.
     *
     */
    public EmptyDataException() {
        super(ERROR_CODE, "Expected not empty data set");
    }
}
