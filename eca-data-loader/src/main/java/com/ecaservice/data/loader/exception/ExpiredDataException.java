package com.ecaservice.data.loader.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.data.loader.dto.DataLoaderApiErrorCode;

/**
 * Exception throws in case if train data has been expired.
 *
 * @author Roman Batygin
 */
public class ExpiredDataException extends ValidationErrorException {

    /**
     * Creates exception object.
     *
     * @param errorMessage - error message
     */
    public ExpiredDataException(String errorMessage) {
        super(DataLoaderApiErrorCode.EXPIRED_DATA, errorMessage);
    }
}
