package com.ecaservice.server.service.client;

import com.ecaservice.common.error.model.ErrorDetails;
import com.ecaservice.server.exception.DataStorageException;
import com.fasterxml.jackson.core.JsonProcessingException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.ecaservice.common.web.util.ValidationErrorHelper.getFirstErrorCodeAsEnum;
import static com.ecaservice.common.web.util.ValidationErrorHelper.retrieveValidationErrors;

/**
 * Feign client error handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class FeignClientErrorHandler {

    /**
     * Handles bad request error.
     *
     * @param uuid         - uuid
     * @param badRequestEx - bad request exception
     * @param clazz        - error code class
     * @param <E>          - error code generic type
     * @return bad request error code
     */
    public <E extends Enum<E> & ErrorDetails> E handleBadRequest(String uuid,
                                                                 FeignException.BadRequest badRequestEx,
                                                                 Class<E> clazz) {
        log.info("Starting to handle bad request error for uuid [{}]", uuid);
        try {
            var validationErrors = retrieveValidationErrors(badRequestEx.contentUTF8());
            var errorCode = getFirstErrorCodeAsEnum(validationErrors, clazz);
            if (errorCode == null) {
                log.error("Got unknown error code for uuid [{}]", uuid);
                throw new DataStorageException(
                        String.format("Got unknown error code for uuid [%s]", uuid));
            } else {
                return errorCode;
            }
        } catch (JsonProcessingException ex) {
            log.error("Got json processing error while handling bad request with status [{}] for uuid [{}]",
                    badRequestEx.status(), uuid);
            throw new DataStorageException(
                    String.format("Parse bad request response error for uuid [%s]", uuid));
        }
    }
}
