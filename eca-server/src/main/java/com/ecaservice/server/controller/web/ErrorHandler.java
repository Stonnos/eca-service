package com.ecaservice.server.controller.web;

import feign.FeignException;
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
     * Handles feign client unauthorized error.
     *
     * @param ex -  exception
     * @return response entity
     */
    @ExceptionHandler(FeignException.Unauthorized.class)
    public ResponseEntity<String> handleFeignUnauthorized(FeignException.Unauthorized ex) {
        log.error("Feign client unauthorized error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
