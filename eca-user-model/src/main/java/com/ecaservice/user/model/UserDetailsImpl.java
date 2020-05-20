package com.ecaservice.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
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
     * User creation date
     */
    @Getter
    @Setter
    private LocalDateTime creationDate;

    /**
     * User email
     */
    @Getter
    @Setter
    private String email;

    /**
     * User first name
     */
    @Getter
    @Setter
    private String firstName;

    /**
     * Roles list
     */
    @Setter
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
}
