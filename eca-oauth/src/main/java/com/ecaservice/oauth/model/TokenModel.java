package com.ecaservice.oauth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenModel {

    /**
     * Token value
     */
    private String token;

    /**
     * User id
     */
    private Long userId;

    /**
     * Token id in database
     */
    private Long tokenId;
}
