package com.ecaservice.oauth.exception;

/**
 * Exception throws in case if password is invalid.
 *
 * @author Roman Batygin
 */
public class InvalidPasswordException extends PasswordException {

    private static final String ERROR_CODE = "InvalidPassword";

    /**
     * Constructor with parameters.
     */
    public InvalidPasswordException() {
        super(ERROR_CODE, "Invalid password");
    }
}
