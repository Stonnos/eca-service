package com.ecaservice.oauth.model;

/**
 * Password rule type.
 *
 * @author Roman Batygin
 */
public enum PasswordRuleType {

    /**
     * Min. length rule
     */
    MIN_LENGTH,

    /**
     * Character rule
     */
    CHARACTER,

    /**
     * Repeat character rule
     */
    REPEAT_CHARACTERS
}
