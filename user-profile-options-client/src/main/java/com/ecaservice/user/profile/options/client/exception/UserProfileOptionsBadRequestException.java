package com.ecaservice.user.profile.options.client.exception;

/**
 * User profile options bad request exception class.
 *
 * @author Roman Batygin
 */
public class UserProfileOptionsBadRequestException extends RuntimeException {

    /**
     * Creates exception.
     *
     * @param message - error message
     */
    public UserProfileOptionsBadRequestException(String message) {
        super(message);
    }
}
