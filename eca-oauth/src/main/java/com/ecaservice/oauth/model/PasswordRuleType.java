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
     * Special character rule
     */
    SPECIAL_CHARACTER,

    /**
     * Digit rule
     */
    DIGIT,

    /**
     * Lower case character rule
     */
    LOWER_CASE_CHARACTER,

    /**
     * Upper case character rule
     */
    UPPER_CASE_CHARACTER,

    /**
     * Repeat character rule
     */
    REPEAT_CHARACTERS
}
