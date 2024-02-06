package com.ecaservice.core.message.template.exception;

/**
 * Message template processing exception.
 *
 * @author Roman Batygin
 */
public class MessageTemplateProcessingException extends RuntimeException {

    /**
     * Creates message template processing exception.
     *
     * @param message - message
     */
    public MessageTemplateProcessingException(String message) {
        super(message);
    }
}
