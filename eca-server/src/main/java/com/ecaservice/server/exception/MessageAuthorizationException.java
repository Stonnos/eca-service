package com.ecaservice.server.exception;

/**
 * Message authorization exception.
 *
 * @author Roman Batygin
 */
public class MessageAuthorizationException extends RuntimeException {

    public MessageAuthorizationException(String message) {
        super(message);
    }
}
