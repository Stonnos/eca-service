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

    /**
     * Role name
     */
    private String authority;

    @Override
    public String getAuthority() {
        return authority;
    }
}
