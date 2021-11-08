package com.ecaservice.oauth.exception;

/**
 * User lock not allowed exception class.
 *
 * @author Roman Batygin
 */
public class UserLockNotAllowedException extends RuntimeException {

    /**
     * Creates exception object.
     */
    public UserLockNotAllowedException() {
        super("User lock not allowed");
    }
}
