package com.ecaservice.server.service.ds;

/**
 * Data storage internal error code.
 *
 * @author Roman Batygin
 */
public enum DsInternalErrorCode {

    /**
     * Class attribute not selected error code
     */
    CLASS_ATTRIBUTE_NOT_SELECTED,

    /**
     * Selected attributes number is too low error code
     */
    SELECTED_ATTRIBUTES_NUMBER_IS_TOO_LOW,

    /**
     * Class values is too low error code
     */
    CLASS_VALUES_IS_TOO_LOW,

    /**
     * Instances not found
     */
    INSTANCES_NOT_FOUND
}
