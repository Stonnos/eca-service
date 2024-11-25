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
    INVALID_TRAIN_DATA_FORMAT("InvalidTrainDataFormat"),

    /**
     * Max. upload size exceeded code
     */
    MAX_UPLOAD_SIZE_EXCEEDED_CODE("MaxUploadSizeExceeded"),

    /**
     * Data not found code
     */
    DATA_NOT_FOUND("DataNotFound"),

    /**
     * Process file error code
     */
    PROCESS_FILE_ERROR("ProcessFileError"),

    /**
     * Invalid file extension code
     */
    INVALID_FILE("InvalidFile");

    /**
     * Error code
     */
    private final String code;
}
