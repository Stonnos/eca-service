package com.ecaservice.server.service.ds;

import com.ecaservice.data.storage.dto.DsErrorCode;
import com.ecaservice.server.exception.DataStorageException;
import com.fasterxml.jackson.core.JsonProcessingException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.ecaservice.common.web.util.ValidationErrorHelper.getFirstErrorCodeAsEnum;
import static com.ecaservice.common.web.util.ValidationErrorHelper.retrieveValidationErrors;

/**
 * Data storage error handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataStorageErrorHandler {

    /**
     * Handles data storage bad request error.
     *
     * @param uuid         - instances uuid
     * @param badRequestEx - exception object
     * @return data storage error code
     */
    public DsErrorCode handleBadRequest(String uuid, FeignException.BadRequest badRequestEx) {
        log.info("Starting to handle ds bad request error for instances [{}]", uuid);
        try {
            var validationErrors = retrieveValidationErrors(badRequestEx.contentUTF8());
            var dsErrorCode = getFirstErrorCodeAsEnum(validationErrors, DsErrorCode.class);
            if (dsErrorCode == null) {
                log.error("Got unknown data storage error code for uuid [{}]", uuid);
                throw new DataStorageException(
                        String.format("Got unknown data storage error code for uuid [%s]", uuid));
            } else {
                return dsErrorCode;
            }
        } catch (JsonProcessingException ex) {
            log.error("Got error while handling bad request with status [{}] for uuid [{}]", badRequestEx.status(),
                    uuid);
            throw new DataStorageException(
                    String.format("Parse bad request response error from data storage for uuid [%s]", uuid));
        }
    }
}
