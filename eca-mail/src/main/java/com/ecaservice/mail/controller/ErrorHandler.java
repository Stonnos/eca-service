package com.ecaservice.mail.controller;

import com.ecaservice.notification.dto.ValidationErrorDto;
import com.google.common.collect.Iterables;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Exception handler for controllers.
 *
 * @author Roman Batygin
 */
@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ErrorHandler {

    /**
     * Handles constraint violation error.
     *
     * @param ex - constraint violation exception
     * @return response entity
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<List<ValidationErrorDto>> handleConstraintViolation(ConstraintViolationException ex) {
        List<ValidationErrorDto> validationErrors = ex.getConstraintViolations().stream()
                .map(constraintViolation -> {
                    Path.Node node = Iterables.getLast(constraintViolation.getPropertyPath());
                    ValidationErrorDto validationErrorDto = new ValidationErrorDto();
                    validationErrorDto.setFieldName(node.getName());
                    validationErrorDto.setErrorMessage(constraintViolation.getMessage());
                    return validationErrorDto;
                }).collect(Collectors.toList());
        return ResponseEntity.badRequest().body(validationErrors);
    }
}
