package com.ecaservice.base.model;

import lombok.Data;

import java.util.List;

/**
 * Response basic model.
 *
 * @author Roman Batygin
 */
@Data
public class EcaResponse {

    /**
     * Request unique identifier
     */
    private String requestId;

    /**
     * Technical status
     */
    private TechnicalStatus status;

    /**
     * Errors list
     */
    private List<Error> errors;
}
