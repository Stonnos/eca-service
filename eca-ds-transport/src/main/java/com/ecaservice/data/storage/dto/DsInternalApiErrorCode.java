package com.ecaservice.data.storage.dto;

import com.ecaservice.common.error.model.ErrorDetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Ds internal api error codes.
 *
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor
public enum DsInternalApiErrorCode implements ErrorDetails {

    /**
     * Class attribute not selected error code
     */
    CLASS_ATTRIBUTE_NOT_SELECTED("ClassAttributeNotSelected"),

    /**
     * Selected attributes number is too low error code
     */
    SELECTED_ATTRIBUTES_NUMBER_IS_TOO_LOW("SelectedAttributesIsTooLow"),

    /**
     * Class values is too low error code
     */
    CLASS_VALUES_IS_TOO_LOW("ClassValuesIsTooLow"),

    /**
     * Instances not found
     */
    INSTANCES_NOT_FOUND("InstancesNotFound");

    /**
     * Error code
     */
    private final String code;
}
