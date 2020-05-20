package com.ecaservice.user.model;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

/**
 * Role model.
 *
 * @author Roman Batygin
 */
@AllArgsConstructor
public class Role implements GrantedAuthority {

    public static final String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN";
    public static final String ROLE_ECA_USER = "ROLE_ECA_USER";

    /**
     * Role name
     */
    private String authority;

    @Override
    public String getAuthority() {
        return authority;
    }
}
