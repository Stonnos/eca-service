package com.ecaservice.user.model;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Implements user details model.
 *
 * @author Roman Batygin
 */
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    /**
     * User login
     */
    private String userName;

    /**
     * User password
     */
    private String password;

    /**
     * User email
     */
    private String email;

    /**
     * User first name
     */
    private String firstName;

    /**
     * Roles list
     */
    private List<Role> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
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
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }
}
