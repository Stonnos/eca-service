package com.ecaservice.mail.error;

import com.ecaservice.common.error.model.ErrorDetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Error code.
 *
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor
public enum EcaMailErrorCode implements ErrorDetails {

    /**
     * Duplicate request id
     */
    DUPLICATE_REQUEST_ID("DuplicateRequestId");

    /**
     * Error code
     */
    private final String code;
}
