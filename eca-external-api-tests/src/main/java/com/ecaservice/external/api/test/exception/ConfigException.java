package com.ecaservice.external.api.test.exception;

/**
 * Config exception.
 *
 * @author Roman Batygin
 */
public class ConfigException extends RuntimeException {

    /**
     * Creates config exception.
     *
     * @param message - error message
     */
    public ConfigException(String message) {
        super(message);
    }
}
