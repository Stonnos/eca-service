package com.ecaservice.web.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Authentication service.
 *
 * @author Roman Batygin
 */
@Service
public class AuthenticationService {

    /**
     * Gets current authentication.
     *
     * @return authentication object
     */
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
