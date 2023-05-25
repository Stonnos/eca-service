package com.ecaservice.common.web;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.common.web.exception.ValidationErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * Global exception handler for controllers.
 *
 * @author Roman Batygin
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles bad request error.
     *
     * @param ex -  exception
     * @return response entity
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> handleBadRequest(Exception ex) {
        log.error("Bad request error: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * Handles validation error.
     *
     * @param ex -  method argument not valid exception
     * @return response entity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationErrorDto>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.error("Method argument not valid error: {}", ex.getMessage());
        var response = ExceptionResponseHandler.handleMethodArgumentNotValid(ex);
        log.error("Method argument not valid errors: {}", response.getBody());
        return response;
    }

    /**
     * Handles constraint violation error.
     *
     * @param ex - constraint violation exception
     * @return response entity
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<List<ValidationErrorDto>> handleConstraintViolation(ConstraintViolationException ex) {
        log.error("Constraint violation error: {}", ex.getMessage());
        var response = ExceptionResponseHandler.handleConstraintViolation(ex);
        log.error("Constraint violation errors: {}", response.getBody());
        return response;
    }

    /**
     * Handles validation error.
     *
     * @param ex -  validation error exception
     * @return response entity
     */
    @ExceptionHandler(ValidationErrorException.class)
    public ResponseEntity<List<ValidationErrorDto>> handleValidationError(ValidationErrorException ex) {
        log.error("Validation error [{}]: {}", ex.getErrorDetails(), ex.getMessage());
        return ExceptionResponseHandler.handleValidationErrorException(ex);
    }

    /**
     * Handles validation error.
     *
     * @param ex -  validation error exception
     * @return response entity
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<List<ValidationErrorDto>> handleHttpMessageNotReadableError(
            HttpMessageNotReadableException ex) {
        log.error("Http message not readable error: {}", ex.getMessage());
        var response = ExceptionResponseHandler.handleHttpMessageNotReadable(ex);
        log.error("Http message not readable errors: {}", response.getBody());
        return response;
    }

    /**
     * Handles constraint violation error.
     *
     * @param ex - constraint violation exception
     * @return response entity
     */
    @ExceptionHandler(value = BindException.class)
    public ResponseEntity<List<ValidationErrorDto>> handleBindException(BindException ex) {
        log.error("Bind error: {}", ex.getMessage());
        var response = ExceptionResponseHandler.handleBindException(ex);
        log.error("Bind errors: {}", response.getBody());
        return response;
    }

    /**
     * Handles method argument type mismatch exception.
     *
     * @param ex - constraint violation exception
     * @return response entity
     */
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<List<ValidationErrorDto>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {
        log.error("Method argument type mismatch error: {}", ex.getMessage());
        var response = ExceptionResponseHandler.handleMethodArgumentTypeMismatchException(ex);
        log.error("Method argument type mismatch errors: {}", response.getBody());
        return response;
    }

    /**
     * Handles {@link MaxUploadSizeExceededException} errors.
     *
     * @param ex - exception
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<List<ValidationErrorDto>> handleError(MaxUploadSizeExceededException ex) {
        log.error("Max upload size error: {}", ex.getMessage());
        var response = ExceptionResponseHandler.handleMaxUploadSizeExceededException(ex);
        log.error("Max upload size error response: {}", response.getBody());
        return response;
    }
}
