package com.ecaservice.external.api.error;

import com.ecaservice.external.api.dto.ResponseCode;
import com.ecaservice.external.api.exception.DataNotFoundException;
import org.springframework.amqp.AmqpException;
import org.springframework.stereotype.Component;

/**
 * Exception translate class.
 *
 * @author Roman Batygin
 */
@Component
public class ExceptionTranslator {

    /**
     * Converts exception to response code.
     *
     * @param ex - exception
     * @return response code
     */
    public ResponseCode translate(Exception ex) {
        if (ex instanceof AmqpException) {
            return ResponseCode.SERVICE_UNAVAILABLE;
        }
        if (ex instanceof DataNotFoundException) {
            return ResponseCode.DATA_NOT_FOUND;
        }
        return ResponseCode.ERROR;
    }
}
