package com.ecaservice.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * User service.
 *
 * @author Roman Batygin
 */
@Service
public class UserService {

    /**
     * Gets current authenticated user.
     *
     * @return user details
     */
    public String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
