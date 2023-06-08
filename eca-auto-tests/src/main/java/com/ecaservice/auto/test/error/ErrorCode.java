package com.ecaservice.auto.test.error;

import com.ecaservice.common.error.model.ErrorDetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Error codes.
 *
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode implements ErrorDetails {

    /**
     * Unsupported test feature.
     */
    UNSUPPORTED_FEATURE("UnsupportedFeature");

    /**
     * Error code
     */
    private final String code;
}
