package com.ecaservice.model;

import lombok.Data;

/**
 * Email model.
 * @author Roman Batygin
 */
@Data
public class Mail {

    private String from;

    private String to;

    private String subject;

    private String message;

    private boolean html;
}
