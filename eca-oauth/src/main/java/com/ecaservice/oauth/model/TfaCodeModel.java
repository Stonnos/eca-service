package com.ecaservice.oauth.model;

import lombok.Builder;
import lombok.Data;

/**
 * Tfa code model.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class TfaCodeModel {

    /**
     * Unique token value for web application
     */
    private String token;

    /**
     * Two - factor authentication code value for email sending
     */
    private String code;
}
