package com.ecaservice.util;

import brave.propagation.ExtraFieldPropagation;
import lombok.experimental.UtilityClass;
import org.slf4j.MDC;

/**
 * Log helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class LogHelper {

    /**
     * Evaluation request id
     */
    public static final String EV_REQUEST_ID = "ev-requestId";

    /**
     * Puts key-value pair in mdc.
     *
     * @param key   - key
     * @param value - value
     */
    public static void putMdc(String key, String value) {
        MDC.put(key, value);
        ExtraFieldPropagation.set(key, value);
    }
}
