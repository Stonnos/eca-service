package com.ecaservice.data.loader.dto;

import com.ecaservice.common.error.model.ErrorDetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Data loader api error codes.
 *
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor
public enum DataLoaderApiErrorCode implements ErrorDetails {

    /**
     * Invalid train data format
     */
    INVALID_TRAIN_DATA_FORMAT("InvalidTrainDataFormat");

    /**
     * Error code
     */
    private final String code;
}
