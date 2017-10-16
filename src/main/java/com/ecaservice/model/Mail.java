package com.ecaservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Email model.
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
