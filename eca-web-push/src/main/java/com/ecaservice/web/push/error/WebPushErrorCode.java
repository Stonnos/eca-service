package com.ecaservice.web.push.error;

import com.ecaservice.common.error.model.ErrorDetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Error code.
 *
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor
public enum WebPushErrorCode implements ErrorDetails {

    /**
     * Invalid notifications ids
     */
    INVALID_NOTIFICATIONS_IDS("InvalidNotificationsIds");

    /**
     * Error code
     */
    private final String code;
}
