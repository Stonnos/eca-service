package com.ecaservice.oauth.exception;

/**
 * Exception throws in case if active change password request already exists.
 *
 * @author Roman Batygin
 */
public class ChangePasswordRequestAlreadyExistsException extends RuntimeException {

    private static final String ERROR_CODE = "ActiveChangePasswordRequest";

    /**
     * Constructor with parameters.
     *
     * @param userId - user id
     */
    public ChangePasswordRequestAlreadyExistsException(Long userId) {
        super(String.format("Active change password request already exists for user [%d]", userId));
    }

    /**
     * Gets error code.
     *
     * @return error code
     */
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
