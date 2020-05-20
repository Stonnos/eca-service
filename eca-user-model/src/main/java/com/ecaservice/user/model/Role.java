package com.ecaservice.user.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

/**
 * Role model.
 *
 * @author Roman Batygin
 */
@NoArgsConstructor
@AllArgsConstructor
public class Role implements GrantedAuthority {

    public static final String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN";
    public static final String ROLE_ECA_USER = "ROLE_ECA_USER";

    /**
     * Role name
     */
    @Setter
    private String authority;

    @Override
    public String getAuthority() {
        return authority;
    }
}
