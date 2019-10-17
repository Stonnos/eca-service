package com.ecaservice.util;

import com.ecaservice.exception.experiment.ExperimentException;
import org.slf4j.Logger;

/**
 * Utility class for experiment logging.
 *
 * @author Roman Batygin
 */
public class ExperimentLogUtils {

    private ExperimentLogUtils() {
    }

    /**
     * Logs error message and throws exception.
     *
     * @param message error message
     * @param logger  logger
     */
    public static void logAndThrowError(String message, Logger logger) {
        if (logger.isErrorEnabled()) {
            logger.error(message);
        }
        throw new ExperimentException(message);
    }
}
