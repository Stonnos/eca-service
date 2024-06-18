package com.ecaservice.oauth.security.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;

/**
 * Tfa code authentication request.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TfaCodeAuthenticationRequest {

    /**
     * Registered client id
     */
    private String clientId;

    /**
     * Auhenticated user object
     */
    private Authentication authentication;
}
