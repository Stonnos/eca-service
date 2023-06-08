package com.ecaservice.core.filter.error;

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
public enum CoreFilterErrorCode implements ErrorDetails {

    /**
     * Invalid value format code
     */
    INVALID_VALUE_FORMAT("InvalidValueFormat"),

    /**
     * Field not found error code
     */
    FIELD_NOT_FOUND("FieldNotFound");

    /**
     * Error code
     */
    private final String code;
}
