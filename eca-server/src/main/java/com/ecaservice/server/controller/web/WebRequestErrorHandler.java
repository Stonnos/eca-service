package com.ecaservice.server.controller.web;

import com.ecaservice.common.error.model.ValidationErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
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
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebRequestErrorHandler {

    /**
     * Handles bpmn error.
     *
     * @param ex -  exception
     * @return response entity
     */
    @ExceptionHandler(BpmnError.class)
    public ResponseEntity<List<ValidationErrorDto>> handleBpmnError(BpmnError ex) {
        log.error("Bpmn error code [{}]: {}", ex.getErrorCode(), ex.getMessage());
        var validationErrorDto = new ValidationErrorDto();
        validationErrorDto.setCode(ex.getErrorCode());
        validationErrorDto.setErrorMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(Collections.singletonList(validationErrorDto));
    }
}
