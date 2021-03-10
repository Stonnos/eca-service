package com.ecaservice.data.storage.controller;

import com.ecaservice.common.web.ExceptionResponseHandler;
import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.common.web.exception.ValidationErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;

/**
 * Exception handler for controllers.
 *
 * @author Roman Batygin
 */
@Slf4j
@ControllerAdvice
public class ErrorHandler {

    /**
     * Handles constraint violation error.
     *
     * @param ex - constraint violation exception
     * @return response entity
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<List<ValidationErrorDto>> handleConstraintViolation(ConstraintViolationException ex) {
        return ExceptionResponseHandler.handleConstraintViolation(ex);
    }

    /**
     * Handles validation error.
     *
     * @param ex -  exception
     * @return response entity
     */
    @ExceptionHandler(ValidationErrorException.class)
    public ResponseEntity<List<ValidationErrorDto>> handleValidationError(ValidationErrorException ex) {
        log.error("Validation error [{}]: {}", ex.getErrorCode(), ex.getMessage());
        return buildBadRequestResponse(ex.getErrorCode());
    }

    private ResponseEntity<List<ValidationErrorDto>> buildBadRequestResponse(String errorCode) {
        ValidationErrorDto validationErrorDto = new ValidationErrorDto();
        validationErrorDto.setCode(errorCode);
        return ResponseEntity.badRequest().body(Collections.singletonList(validationErrorDto));
    }
}
