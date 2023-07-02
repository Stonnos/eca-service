package com.ecaservice.data.storage.error;

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
public enum DsErrorCode implements ErrorDetails {

    /**
     * Empty data set code
     */
    EMPTY_DATA_SET("EmptyDataSet"),

    /**
     * Invalid class attribute type error code
     */
    INVALID_CLASS_ATTRIBUTE_TYPE("InvalidClassAttributeType"),

    /**
     * Instances name duplication error code
     */
    INSTANCES_NAME_DUPLICATION("DuplicateInstancesName");

    /**
     * Error code
     */
    private final String code;
}
