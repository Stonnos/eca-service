package com.ecaservice.oauth.model;

/**
 * Password rule visitor.
 *
 * @param <T> - result generic type
 * @author Roman Batygin
 */
public interface PasswordRuleVisitor<T> {

    /**
     * Visit min. length rule.
     *
     * @param minLengthRule - min length rule
     * @return result
     */
    T handle(MinLengthRule minLengthRule);

    /**
     * Visit special character rule.
     *
     * @param specialCharacterRule - character rule
     * @return result
     */
    T handle(SpecialCharacterRule specialCharacterRule);

    /**
     * Visit special character rule.
     *
     * @param digitRule - character rule
     * @return result
     */
    T handle(DigitRule digitRule);

    /**
     * Visit special character rule.
     *
     * @param lowerCaseCharacterRule - lower case character rule
     * @return result
     */
    T handle(LowerCaseCharacterRule lowerCaseCharacterRule);

    /**
     * Visit special character rule.
     *
     * @param upperCaseCharacterRule - upper case character rule
     * @return result
     */
    T handle(UpperCaseCharacterRule upperCaseCharacterRule);

    /**
     * Visit repeat characters rule.
     *
     * @param repeatCharacterRule - repeat characters rule
     * @return result
     */
    T handle(RepeatCharacterRule repeatCharacterRule);
}
