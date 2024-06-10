package com.ecaservice.oauth.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Security context provider service.
 *
 * @author Roman Batygin
 */
@Service
public class SecurityContextProvider {

    /**
     * Gets current authentication.
     *
     * @return authentication object
     */
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
