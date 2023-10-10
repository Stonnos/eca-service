package com.ecaservice.oauth.controller;

import com.ecaservice.oauth.exception.NotSafePasswordException;
import com.ecaservice.oauth.exception.UserLockNotAllowedException;
import com.ecaservice.web.dto.model.PasswordRuleResultDto;
import com.ecaservice.web.dto.model.PasswordValidationErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Exception handler for controllers.
 *
 * @author Roman Batygin
 */
@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ErrorHandler {

    /**
     * Handles user lock now allowed error.
     *
     * @param ex -  exception
     * @return response entity
     */
    @ExceptionHandler(UserLockNotAllowedException.class)
    public ResponseEntity<String> handleUserLockNotAllowed(UserLockNotAllowedException ex) {
        log.error("User lock now allowed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Handles not safe password exception.
     *
     * @param ex -  exception
     * @return response entity
     */
    @ExceptionHandler(NotSafePasswordException.class)
    public ResponseEntity<List<PasswordValidationErrorDto>> handleNotSafePasswordException(NotSafePasswordException ex) {
        log.error("Not safe password error: {}", ex.getMessage());
        var details = ex.getDetails()
                .stream()
                .map(ruleResultDetails -> new PasswordRuleResultDto(ruleResultDetails.getRuleType().name(),
                        ruleResultDetails.isValid(), ruleResultDetails.getMessage()))
                .collect(Collectors.toList());
        var passwordValidationErrorDto = new PasswordValidationErrorDto();
        passwordValidationErrorDto.setCode(ex.getErrorDetails().getCode());
        passwordValidationErrorDto.setFieldName(ex.getFieldName());
        passwordValidationErrorDto.setDetails(details);
        passwordValidationErrorDto.setErrorMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(Collections.singletonList(passwordValidationErrorDto));
    }
}
