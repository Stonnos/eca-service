package com.ecaservice.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Error model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Error {

    /**
     * Error code
     */
    private String code;

    /**
     * Error field
     */
    private String fieldName;

    /**
     * Error message
     */
    private String message;
}
