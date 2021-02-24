package com.ecaservice.oauth.controller;

import com.ecaservice.common.web.ExceptionResponseHandler;
import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.oauth.exception.PasswordException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
     * Handles validation error.
     *
     * @param ex -  method argument not valid exception
     * @return response entity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationErrorDto>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        return ExceptionResponseHandler.handleMethodArgumentNotValid(ex);
    }

    /**
     * Handles password error.
     *
     * @param ex -  exception
     * @return response entity
     */
    @ExceptionHandler(PasswordException.class)
    public ResponseEntity<List<ValidationErrorDto>> handlePasswordError(PasswordException ex) {
        log.error("Password operation error: {}", ex.getMessage());
        return buildBadRequestResponse(ex.getErrorCode());
    }

    /**
     * Handles bad request error.
     *
     * @param ex -  exception
     * @return response entity
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> handleBadRequest(Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    private ResponseEntity<List<ValidationErrorDto>> buildBadRequestResponse(String errorCode) {
        ValidationErrorDto validationErrorDto = new ValidationErrorDto();
        validationErrorDto.setCode(errorCode);
        return ResponseEntity.badRequest().body(Collections.singletonList(validationErrorDto));
    }
}
