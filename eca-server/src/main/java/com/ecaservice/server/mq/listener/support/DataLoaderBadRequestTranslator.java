package com.ecaservice.server.mq.listener.support;

import com.ecaservice.base.model.EcaResponse;
import com.ecaservice.base.model.ErrorCode;
import com.ecaservice.data.loader.dto.DataLoaderApiErrorCode;
import com.ecaservice.server.exception.DataLoaderBadRequestException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static com.ecaservice.server.util.Utils.buildErrorResponse;
import static com.ecaservice.server.util.Utils.buildValidationError;
import static com.ecaservice.server.util.Utils.error;

/**
 * Data loader bad request exception handler.
 *
 * @author Roman Batygin
 */
@Component
@ConditionalOnProperty(value = "rabbit.enabled", havingValue = "true")
public class DataLoaderBadRequestTranslator extends AbstractExceptionTranslator<DataLoaderBadRequestException> {

    /**
     * Default constructor.
     */
    public DataLoaderBadRequestTranslator() {
        super(DataLoaderBadRequestException.class);
    }

    @Override
    public EcaResponse translate(DataLoaderBadRequestException exception) {
        if (DataLoaderApiErrorCode.DATA_NOT_FOUND.equals(exception.getApiErrorCode()) ||
                DataLoaderApiErrorCode.EXPIRED_DATA.equals(exception.getApiErrorCode())) {
            var messageError = error(ErrorCode.TRAINING_DATA_NOT_FOUND);
            return buildValidationError(Collections.singletonList(messageError));
        } else {
            return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
