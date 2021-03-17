package com.ecaservice.common.web.util;

import brave.propagation.ExtraFieldPropagation;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

/**
 * Log helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class LogHelper {

    /**
     * Transaction id
     */
    public static final String TX_ID = "tx.id";

    /**
     * Evaluation request id
     */
    public static final String EV_REQUEST_ID = "ev.requestId";

    /**
     * Puts key-value pair in mdc.
     *
     * @param key   - key
     * @param value - value
     */
    public static void putMdc(String key, String value) {
        if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(value)) {
            MDC.put(key, value);
            ExtraFieldPropagation.set(key, value);
        }
    }

    /**
     * Gets value by key from mdc.
     *
     * @param key - key
     * @return value associated with key
     */
    public static String getMdc(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        return MDC.get(key);
    }

    /**
     * Puts key-value pair in mdc if it doesn't exists.
     *
     * @param key   - key
     * @param value - value
     */
    public static void putMdcIfAbsent(String key, String value) {
        if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(value)) {
            String val = MDC.get(key);
            if (StringUtils.isEmpty(val)) {
                putMdc(key, value);
            }
        }
    }
}
