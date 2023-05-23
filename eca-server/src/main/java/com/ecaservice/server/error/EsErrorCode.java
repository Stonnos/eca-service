package com.ecaservice.server.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Eca server error code enums.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
@Getter
public enum EsErrorCode {

    /**
     * Class attribute not selected error code
     */
    CLASS_ATTRIBUTE_NOT_SELECTED("ClassAttributeNotSelected"),

    /**
     * Selected attributes number is too low error code
     */
    SELECTED_ATTRIBUTES_NUMBER_IS_TOO_LOW("SelectedAttributesNumberIsTooLow"),

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
    public final String code;
}
