package com.ecaservice.common.web;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.google.common.collect.Iterables;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Exception handler for controllers.
 *
 * @author Roman Batygin
 */
public class ExceptionResponseHandler {

    /**
     * Handles validation error.
     *
     * @param ex -  method argument not valid exception
     * @return response entity
     */
    public static ResponseEntity<List<ValidationErrorDto>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {
        List<ValidationErrorDto> errors = ex.getBindingResult().getAllErrors()
                .stream()
                .map(FieldError.class::cast)
                .map(fieldError -> new ValidationErrorDto(fieldError.getField(),
                        fieldError.getDefaultMessage())).collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handles constraint violation error.
     *
     * @param ex - constraint violation exception
     * @return response entity
     */
    public static ResponseEntity<List<ValidationErrorDto>> handleConstraintViolation(ConstraintViolationException ex) {
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
