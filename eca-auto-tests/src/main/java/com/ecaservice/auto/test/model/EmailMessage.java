package com.ecaservice.auto.test.model;

import lombok.Data;

/**
 * Email message model.
 *
 * @author Roman Batygin
 */
@Data
public class EmailMessage {

    /**
     * Email type
     */
    private EmailType emailType;

    /**
     * Experiment request id
     */
    private String requestId;

    /**
     * Experiment download url
     */
    private String downloadUrl;
}
