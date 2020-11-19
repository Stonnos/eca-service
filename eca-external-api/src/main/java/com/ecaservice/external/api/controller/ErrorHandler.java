package com.ecaservice.external.api.controller;

import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.dto.ValidationErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

import static com.ecaservice.external.api.util.Utils.buildResponse;

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
    public ResponseEntity<ResponseDto<List<ValidationErrorDto>>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {
        List<ValidationErrorDto> errors = ex.getBindingResult().getAllErrors().stream()
                .map(FieldError.class::cast)
                .map(fieldError -> new ValidationErrorDto(fieldError.getField(),
                        fieldError.getDefaultMessage())).collect(Collectors.toList());
        ResponseDto<List<ValidationErrorDto>> responseDto = buildResponse(RequestStatus.VALIDATION_ERROR, errors);
        return ResponseEntity.ok(responseDto);
    }
}
