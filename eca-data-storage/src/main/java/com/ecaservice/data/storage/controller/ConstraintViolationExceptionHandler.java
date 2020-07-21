package com.ecaservice.data.storage.controller;

import com.ecaservice.web.dto.model.ValidationErrorDto;
import com.google.common.collect.Iterables;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Constraint violation exception handler.
 *
 * @author Roman Batygin
 */
@ControllerAdvice
public class ConstraintViolationExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles constraint violation error.
     *
     * @param ex      - constraint violation exception
     * @param request - web request object
     * @return response entity
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        List<ValidationErrorDto> validationErrors = ex.getConstraintViolations().stream()
                .map(constraintViolation -> {
                    Path.Node node = Iterables.getLast(constraintViolation.getPropertyPath());
                    ValidationErrorDto validationErrorDto = new ValidationErrorDto();
                    validationErrorDto.setFieldName(node.getName());
                    validationErrorDto.setCode(
                            constraintViolation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName());
                    validationErrorDto.setErrorMessage(constraintViolation.getMessage());
                    return validationErrorDto;
                }).collect(Collectors.toList());
        return handleExceptionInternal(ex, validationErrors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
