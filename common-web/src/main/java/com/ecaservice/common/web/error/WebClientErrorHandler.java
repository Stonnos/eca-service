package com.ecaservice.common.web.error;

import com.ecaservice.common.error.model.ErrorDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

import static com.ecaservice.common.web.util.ValidationErrorHelper.getFirstErrorCodeAsEnum;
import static com.ecaservice.common.web.util.ValidationErrorHelper.retrieveValidationErrors;

/**
 * Web client error handler.
 *
 * @author Roman Batygin
 */
@Slf4j
public class WebClientErrorHandler {

    /**
     * Handles bad request error. Gets first validation error code as enum
     *
     * @param uuid         - request uuid
     * @param responseBody - response body
     * @param clazz        - error code class
     * @param <E>          - error code generic type
     * @return bad request error code
     */
    public <E extends Enum<E> & ErrorDetails> E handleBadRequest(String uuid,
                                                                 String responseBody,
                                                                 Class<E> clazz) {
        log.info("Starting to handle bad request error for uuid [{}]", uuid);
        try {
            var validationErrors = retrieveValidationErrors(responseBody);
            var errorCode = getFirstErrorCodeAsEnum(validationErrors, clazz);
            if (errorCode == null) {
                log.error("Got unknown error code for uuid [{}]", uuid);
                throw new IllegalStateException(
                        String.format("Got unknown error code for uuid [%s]", uuid));
            } else {
                return errorCode;
            }
        } catch (JsonProcessingException ex) {
            log.error("Got json processing error while handling bad request for uuid [{}]", uuid);
            throw new IllegalStateException(
                    String.format("Parse bad request response error for uuid [%s]", uuid));
        }
    }
}
