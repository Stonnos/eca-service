package com.ecaservice.external.api.controller;

import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.dto.ValidationErrorDto;
import com.google.common.collect.Iterables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.List;
import java.util.stream.Collectors;

import static com.ecaservice.external.api.util.Utils.buildResponse;

/**
 * Exception handler for controllers.
 *
 * @author Roman Batygin
 */
@Slf4j
@ControllerAdvice
public class ErrorHandler {

    /**
     * Handles validation error.
     *
     * @param ex -  method argument not valid exception
     * @return response entity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<List<ValidationErrorDto>>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {
        List<ValidationErrorDto> errors = ex.getBindingResult().getAllErrors().stream()
                .map(FieldError.class::cast)
                .map(fieldError -> new ValidationErrorDto(fieldError.getField(),
                        fieldError.getDefaultMessage())).collect(Collectors.toList());
        ResponseDto<List<ValidationErrorDto>> responseDto = buildResponse(RequestStatus.VALIDATION_ERROR, errors);
        return ResponseEntity.ok(responseDto);
    }

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
