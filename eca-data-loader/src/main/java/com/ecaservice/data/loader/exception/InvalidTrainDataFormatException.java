package com.ecaservice.data.loader.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.data.loader.dto.DataLoaderApiErrorCode;

/**
 * Exception throws in case if train data format is invalid.
 *
 * @author Roman Batygin
 */
public class InvalidTrainDataFormatException extends ValidationErrorException {

    /**
     * Creates exception object.
     *
     * @param errorMessage - error message
     */
    public InvalidTrainDataFormatException(String errorMessage) {
        super(DataLoaderApiErrorCode.INVALID_TRAIN_DATA_FORMAT, errorMessage);
    }
}
