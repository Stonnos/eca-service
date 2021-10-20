package com.ecaservice.mail.config.metrics;

import lombok.experimental.UtilityClass;

/**
 * Metrics constants.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class MetricConstants {

    private static final String BASE_METRIC_PREFIX = "eca-mail";

    /**
     * Sending email messages errors total count
     */
    public static final String SENDING_EMAIL_MESSAGE_ERRORS_METRIC = BASE_METRIC_PREFIX +
            ".sending.message.error.total";

    /**
     * Sending email messages success total count
     */
    public static final String SENDING_EMAIL_MESSAGE_SUCCESS_METRIC = BASE_METRIC_PREFIX +
            ".sending.message.success.total";
}
