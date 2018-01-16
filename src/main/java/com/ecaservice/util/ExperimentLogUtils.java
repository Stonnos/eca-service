package com.ecaservice.util;

import com.ecaservice.exception.ExperimentException;
import org.slf4j.Logger;

/**
 * Utility class for logging.
 *
 * @author Roman Batygin
 */
public class ExperimentLogUtils {

    /**
     * Logs error message and throws exception.
     *
     * @param message error message
     * @param logger  {@link Logger} object
     */
    public static void error(String message, Logger logger) {
        if (logger.isErrorEnabled()) {
            logger.error(message);
        }
        throw new ExperimentException(message);
    }
}
