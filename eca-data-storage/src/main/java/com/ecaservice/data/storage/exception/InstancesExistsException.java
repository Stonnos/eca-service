package com.ecaservice.data.storage.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.data.storage.error.DsErrorCode;

/**
 * Exception throws in case if instances name is exists.
 *
 * @author Roman Batygin
 */
public class InstancesExistsException extends ValidationErrorException {

    /**
     * Creates exception object.
     *
     * @param relationName - relation name
     */
    public InstancesExistsException(String relationName) {
        super(DsErrorCode.INSTANCES_NAME_DUPLICATION,
                String.format("Instances with name [%s] already exists!", relationName));
    }
}
