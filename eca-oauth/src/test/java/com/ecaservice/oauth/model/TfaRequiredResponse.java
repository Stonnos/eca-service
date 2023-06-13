package com.ecaservice.oauth.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Tfa required response.
 *
 * @author Roman Batygin
 */
@Data
public class TfaRequiredResponse {

    /**
     * Error code
     */
    private String error;

    /**
     * Error description
     */
    @JsonProperty("error_description")
    private String errorDescription;

    /**
     * Token value
     */
    private String token;

    /**
     * Tfa code expiration in seconds
     */
    @JsonProperty("expires_in")
    private Integer expiresIn;
}
