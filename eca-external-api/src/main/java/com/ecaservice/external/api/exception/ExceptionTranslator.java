package com.ecaservice.external.api.exception;

import com.ecaservice.external.api.dto.RequestStatus;
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
     * Converts exception to request status.
     *
     * @param ex - exception
     * @return request status
     */
    public RequestStatus translate(Exception ex) {
        if (ex instanceof AmqpException) {
            return RequestStatus.SERVICE_UNAVAILABLE;
        }
        return RequestStatus.ERROR;
    }
}
