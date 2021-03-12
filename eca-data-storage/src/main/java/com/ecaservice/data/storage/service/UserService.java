package com.ecaservice.data.storage.service;

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
     * @return user login
     */
    public String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
