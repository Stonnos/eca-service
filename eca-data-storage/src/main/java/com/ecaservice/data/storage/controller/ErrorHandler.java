package com.ecaservice.data.storage.controller;

import com.ecaservice.data.storage.exception.EntityNotFoundException;
import com.ecaservice.web.dto.model.ValidationErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<ValidationErrorDto>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<ValidationErrorDto> errors = ex.getBindingResult().getAllErrors().stream()
                .map(FieldError.class::cast)
                .map(fieldError -> new ValidationErrorDto(fieldError.getField(), fieldError.getCode(),
                        fieldError.getDefaultMessage())).collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handles bad request error.
     *
     * @param ex -  exception
     * @return response entity
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.notFound().build();
    }
}
