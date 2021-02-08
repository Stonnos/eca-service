package com.ecaservice.oauth.controller;

import com.ecaservice.common.web.ExceptionResponseHandler;
import com.ecaservice.common.web.dto.ValidationErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
}
