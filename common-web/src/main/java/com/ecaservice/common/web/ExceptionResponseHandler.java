package com.ecaservice.common.web;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.common.collect.Iterables;
import lombok.experimental.UtilityClass;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Exception handler for controllers.
 *
 * @author Roman Batygin
 */
@UtilityClass
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
                .map(fieldError -> new ValidationErrorDto(fieldError.getField(), fieldError.getCode(),
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
                    String code = Optional.ofNullable(constraintViolation.getConstraintDescriptor())
                            .map(ConstraintDescriptor::getAnnotation)
                            .map(Annotation::annotationType)
                            .map(Class::getSimpleName).orElse(null);
                    validationErrorDto.setCode(code);
                    validationErrorDto.setErrorMessage(constraintViolation.getMessage());
                    return validationErrorDto;
                }).collect(Collectors.toList());
        return ResponseEntity.badRequest().body(validationErrors);
    }

    /**
     * Handles http message not readable errors.
     *
     * @param ex - http message not readable exception
     * @return validation errors
     */
    public static ResponseEntity<List<ValidationErrorDto>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {
        List<ValidationErrorDto> validationErrors = new ArrayList<>();
        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException invalidFormatException = (InvalidFormatException) ex.getCause();
            for (var reference : invalidFormatException.getPath()) {
                ValidationErrorDto validationErrorDto = new ValidationErrorDto();
                validationErrorDto.setFieldName(reference.getFieldName());
                validationErrorDto.setErrorMessage(ex.getMessage());
                validationErrors.add(validationErrorDto);
            }
        }
        return ResponseEntity.badRequest().body(validationErrors);
    }

    /**
     * Handles bind error.
     *
     * @param ex -  bind exception
     * @return response entity
     */
    public static ResponseEntity<List<ValidationErrorDto>> handleBindException(BindException ex) {
        List<ValidationErrorDto> errors = ex.getAllErrors()
                .stream()
                .map(FieldError.class::cast)
                .map(fieldError -> new ValidationErrorDto(fieldError.getField(), fieldError.getCode(),
                        fieldError.getDefaultMessage())).collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);
    }
}
