package com.ecaservice.oauth.controller;

import com.ecaservice.oauth.exception.UserLockNotAllowedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
     * Handles user lock now allowed error.
     *
     * @param ex -  exception
     * @return response entity
     */
    @ExceptionHandler(UserLockNotAllowedException.class)
    public ResponseEntity<String> handleUserLockNotAllowed(UserLockNotAllowedException ex) {
        log.warn("User lock now allowed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
