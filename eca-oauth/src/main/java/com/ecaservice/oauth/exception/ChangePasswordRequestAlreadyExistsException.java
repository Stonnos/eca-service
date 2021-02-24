package com.ecaservice.oauth.exception;

/**
 * Exception throws in case if active change password request already exists.
 *
 * @author Roman Batygin
 */
public class ChangePasswordRequestAlreadyExistsException extends PasswordException {

    private static final String ERROR_CODE = "ActiveChangePasswordRequest";

    /**
     * Constructor with parameters.
     *
     * @param userId - user id
     */
    public ChangePasswordRequestAlreadyExistsException(Long userId) {
        super(ERROR_CODE, String.format("Active change password request already exists for user [%d]", userId));
    }
}
