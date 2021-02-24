package com.ecaservice.oauth.exception;

/**
 * Exception throws in case if token is invalid.
 *
 * @author Roman Batygin
 */
public class InvalidTokenException extends RuntimeException {

    private static final String ERROR_CODE = "InvalidToken";

    /**
     * Constructor with parameters.
     *
     * @param token - token value
     */
    public InvalidTokenException(String token) {
        super(String.format("Invalid token [%s]", token));
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
