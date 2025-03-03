package com.ecaservice.server.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.server.error.EsErrorCode;

/**
 * Model deleted exception.
 *
 * @author Roman Batygin
 */
public class ModelDeletedException extends ValidationErrorException {

    /**
     * Creates exception.
     */
    public ModelDeletedException() {
        super(EsErrorCode.MODEL_DELETED, "Model has been deleted");
    }
}
