package com.ecaservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Email model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mail {

    /**
     * Sender email
     */
    private String sender;

    /**
     * Receiver email
     */
    private String receiver;

    /**
     * Subject message
     */
    private String subject;

    /**
     * Message text
     */
    private String message;

    /**
     * Is html format?
     */
    private boolean html;
}
