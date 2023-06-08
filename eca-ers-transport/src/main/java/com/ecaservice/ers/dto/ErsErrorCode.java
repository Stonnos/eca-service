package com.ecaservice.ers.dto;

import com.ecaservice.common.error.model.ErrorDetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * ERS error code.
 *
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor
public enum ErsErrorCode implements ErrorDetails {

    /**
     * Duplicate request id
     */
    DUPLICATE_REQUEST_ID("DuplicateRequestId"),

    /**
     * Training data not found
     */
    DATA_NOT_FOUND("DataNotFound"),

    /**
     * Results not found
     */
    RESULTS_NOT_FOUND("ResultsNotFound");

    /**
     * Error code
     */
    private final String code;
}
