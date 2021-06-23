package com.ecaservice.oauth.model;

import lombok.Builder;
import lombok.Data;

/**
 * Token model.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class TokenModel {

    /**
     * Token value
     */
    private String token;

    /**
     * User login
     */
    private String login;

    /**
     * User email
     */
    private String email;

    /**
     * Token id in database
     */
    private Long tokenId;
}
