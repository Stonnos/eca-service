package com.ecaservice.service.experiment.mail;

import com.ecaservice.dto.mail.EmailResponse;
import com.ecaservice.dto.mail.ResponseStatus;
import com.ecaservice.exception.notification.ErrorStatusException;
import com.ecaservice.exception.notification.InvalidRequestParamsException;
import com.ecaservice.exception.notification.UnknownErrorStatusException;
import com.ecaservice.handler.ResponseErrorHandler;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Notification response error handler.
 *
 * @author Roman Batygin
 */
public class NotificationResponseErrorHandler implements ResponseErrorHandler<EmailResponse> {

    private static final Map<ResponseStatus, String> statusMapping =
            ImmutableMap.of(ResponseStatus.ERROR, "Unknown server error", ResponseStatus.INVALID_REQUEST_PARAMS,
                    "Invalid request params");

    @Override
    public boolean hasError(EmailResponse response) {
        return !ResponseStatus.SUCCESS.equals(response.getStatus());
    }

    @Override
    public void handleError(EmailResponse response) {
        switch (response.getStatus()) {
            case ERROR:
                throw new ErrorStatusException(statusMapping.get(response.getStatus()));
            case INVALID_REQUEST_PARAMS:
                throw new InvalidRequestParamsException(statusMapping.get(response.getStatus()));
            default:
                throw new UnknownErrorStatusException(
                        String.format("Received unknown status [%s] from notification service", response.getStatus()));
        }
    }
}
