package com.ecaservice.data.storage.service;

import com.ecaservice.user.model.UserDetailsImpl;
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
    public UserDetailsImpl getCurrentUser() {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
