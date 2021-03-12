package com.ecaservice.listener.support;

import com.ecaservice.base.model.EcaResponse;
import com.ecaservice.base.model.MessageError;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ecaservice.util.Utils.buildValidationError;

@Component
public class ValidationErrorTranslator extends AbstractExceptionTranslator<MethodArgumentNotValidException> {

    /**
     * Default constructor.
     */
    public ValidationErrorTranslator() {
        super(MethodArgumentNotValidException.class);
    }

    @Override
    public EcaResponse translate(MethodArgumentNotValidException exception) {
        List<ObjectError> objectErrors = Optional.ofNullable(exception.getBindingResult())
                .map(BindingResult::getAllErrors)
                .orElse(Collections.emptyList());
        List<MessageError> errors = objectErrors
                .stream()
                .map(FieldError.class::cast)
                .map(fieldError -> new MessageError(fieldError.getCode(), fieldError.getField(),
                        fieldError.getDefaultMessage())).collect(Collectors.toList());
       return buildValidationError(errors);
    }
}
