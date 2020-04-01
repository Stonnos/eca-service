package com.ecaservice.controller.web;

import com.ecaservice.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Exception handler for controllers.
 *
 * @author Roman Batygin
 */
@Slf4j
@ControllerAdvice
public class ErrorHandler {

    /**
     * Handles bad request error.
     *
     * @param ex -  exception
     * @return response entity
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class, EntityNotFoundException.class})
    public ResponseEntity handleBadRequest(Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
