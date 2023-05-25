package com.ecaservice.common.web.error;

import com.ecaservice.common.error.model.ErrorDetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Common error codes enum.
 *
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorDetails {

    /**
     * Invalid request code
     */
    INVALID_REQUEST_CODE("InvalidRequest"),

    /**
     * Invalid request format code
     */
    INVALID_FORMAT_CODE("InvalidFormat"),

    /**
     * Max. upload size exceeded code
     */
    MAX_UPLOAD_SIZE_EXCEEDED_CODE("MaxUploadSizeExceeded"),

    /**
     * Invalid operation code
     */
    INVALID_OPERATION("InvalidOperation"),

    /**
     * Data not found code
     */
    DATA_NOT_FOUND("DataNotFound"),

    /**
     * Process file error code
     */
    PROCESS_FILE_ERROR("ProcessFileError"),

    /**
     * Invalid file code
     */
    INVALID_FILE("InvalidFile");

    /**
     * Error code
     */
    private final String code;
}
