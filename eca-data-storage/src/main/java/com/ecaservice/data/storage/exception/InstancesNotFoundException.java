package com.ecaservice.data.storage.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.data.storage.dto.DsInternalApiErrorCode;

/**
 * Instances not found exception class.
 *
 * @author Roman Batygin
 */
public class InstancesNotFoundException extends ValidationErrorException {

    /**
     * Creates results not found exception.
     *
     * @param message - error message
     */
    public InstancesNotFoundException(String message) {
        super(DsInternalApiErrorCode.INSTANCES_NOT_FOUND, message);
    }
}
