package com.ecaservice.server.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.server.error.EsErrorCode;

/**
 * Ensemble classifier options not allowed exception.
 *
 * @author Roman Batygin
 */
public class EnsembleClassifierOptionsNotAllowedException extends ValidationErrorException {

    /**
     * Creates exception.
     */
    public EnsembleClassifierOptionsNotAllowedException() {
        super(EsErrorCode.ENSEMBLE_CLASSIFIER_OPTIONS_NOT_ALLOWED, "Ensemble classifier options not allowed");
    }
}
