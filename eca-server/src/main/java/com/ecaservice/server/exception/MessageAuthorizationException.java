package com.ecaservice.server.exception;

import lombok.Getter;
import org.springframework.amqp.core.Message;

/**
 * Message authorization exception.
 *
 * @author Roman Batygin
 */
public class MessageAuthorizationException extends RuntimeException {

    @Getter
    private final Message failedMessage;

    /**
     * Creates exception object.
     *
     * @param message       - message text
     * @param failedMessage - failed amqp message
     */
    public MessageAuthorizationException(String message, Message failedMessage) {
        super(message);
        this.failedMessage = failedMessage;
    }
}
