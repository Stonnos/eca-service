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
     * @param minLengthRuleModel - min length rule model
     * @return result
     */
    T handle(MinLengthRuleModel minLengthRuleModel);

    /**
     * Visit character rule.
     *
     * @param characterRuleModel - character rule model
     * @return result
     */
    T handle(CharacterRuleModel characterRuleModel);

    /**
     * Visit repeat characters rule.
     *
     * @param repeatCharactersRuleModel - repeat characters rule model
     * @return result
     */
    T handle(RepeatCharactersRuleModel repeatCharactersRuleModel);
}
