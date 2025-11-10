package com.ecaservice.server.mq.listener;

import lombok.experimental.UtilityClass;

/**
 * Message headers.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class MessageHeaders {

    /**
     * Authentication token header
     */
    public static final String AUTH_TOKEN_HEADER = "auth-token";

    /**
     * User header
     */
    public static final String USER_HEADER = "user";
}
