package com.ecaservice.core.filter.error;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.List;

/**
 * Filter exception handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@ControllerAdvice
public class FilterExceptionHandler {

    /**
     * Handles {@link PropertyReferenceException} errors.
     *
     * @param ex - property reference exception
     */
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<List<ValidationErrorDto>> handleError(PropertyReferenceException ex) {
        log.error("Property reference error for name [{}]: {}", ex.getPropertyName(), ex.getMessage());
        var validationError =
                new ValidationErrorDto(ex.getPropertyName(), CoreFilterErrorCode.FIELD_NOT_FOUND.getCode(),
                        ex.getMessage());
        return ResponseEntity.badRequest().body(Collections.singletonList(validationError));
    }
}
