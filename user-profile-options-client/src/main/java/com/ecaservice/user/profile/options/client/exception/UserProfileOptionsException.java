package com.ecaservice.user.profile.options.client.exception;

/**
 * User profile options exception class.
 *
 * @author Roman Batygin
 */
public class UserProfileOptionsException extends RuntimeException {

    /**
     * Creates exception.
     *
     * @param message - error message
     */
    public UserProfileOptionsException(String message) {
        super(message);
    }
}
