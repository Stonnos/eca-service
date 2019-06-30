package com.ecaservice.user.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Implements user details model.
 *
 * @author Roman Batygin
 */
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
     * Creates user details with specified user name and password.
     *
     * @param userName  - user name
     * @param password  - password
     * @param email     - user email
     * @param firstName - first name
     */
    public UserDetailsImpl(String userName, String password, String email, String firstName) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
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
