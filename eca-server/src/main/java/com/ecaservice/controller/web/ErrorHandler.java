package com.ecaservice.controller.web;

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

    /**
     * Handles feign client unauthorized error.
     *
     * @param ex -  exception
     * @return response entity
     */
    @ExceptionHandler(FeignException.Unauthorized.class)
    public ResponseEntity<String> handleFeignUnauthorized(FeignException.Unauthorized ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
