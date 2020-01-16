package com.ecaservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Web config dto model.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
public class WebConfigDto {

    /**
     * Server api url
     */
    private String apiUrl;

    /**
     * Auth server url
     */
    private String oauthUrl;

    /**
     * Client id
     */
    private String clientId;

    /**
     * Client secret
     */
    private String secret;
}