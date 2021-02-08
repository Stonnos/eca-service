package com.ecaservice.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Validation error dto.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorDto {

    /**
     * Field name
     */
    private String fieldName;

    /**
     * Error message
     */
    private String errorMessage;
}
