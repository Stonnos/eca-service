package com.ecaservice.model;

import lombok.Data;

/**
 * Email model.
 * @author Roman Batygin
 */
@Data
public class Mail {

    /**
     * Source email
     */
    private String from;

    /**
     * Target email
     */
    private String to;

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
