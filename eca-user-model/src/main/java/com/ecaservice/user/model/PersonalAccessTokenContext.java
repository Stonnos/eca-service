package com.ecaservice.user.model;

import com.ecaservice.user.dto.PersonalAccessTokenType;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

/**
 * Personal access token context.
 *
 * @author Roman Batygin
 */
@Getter
public class PersonalAccessTokenContext extends AbstractAuthenticationToken {

    /**
     * User login
     */
    private final String user;

    /**
     * Token type
     */
    private final PersonalAccessTokenType tokenType;

    /**
     * Creates personal access token context.
     */
    public PersonalAccessTokenContext(String user, PersonalAccessTokenType tokenType) {
        super(Collections.emptyList());
        this.user = user;
        this.tokenType = tokenType;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }
}
