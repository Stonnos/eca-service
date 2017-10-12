package com.ecaservice.dto;

import com.ecaservice.model.TechnicalStatus;
import lombok.Data;

/**
 * Response basic model.
 * @author Roman Batygin
 */
@Data
public class EcaResponse {

    /**
     * Technical status
     */
    private TechnicalStatus status;

    /**
     * Error message
     */
    private String errorMessage;
}
