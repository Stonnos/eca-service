package com.ecaservice.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Implements user details model.
 *
 * @author Roman Batygin
 */
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    /**
     * User id
     */
    @Setter
    @Getter
    private Long id;

    /**
     * User login
     */
    @Setter
    private String userName;

    /**
     * User password
     */
    @Setter
    private String password;

    /**
     * Roles list
     */
    @Setter
    private List<Role> authorities;

    /**
     * Account locked?
     */
    @Setter
    private boolean locked;

    @Override
    public Collection<Role> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
