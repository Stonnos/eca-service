package com.ecaservice.oauth.model;

/**
 * Special character rule.
 *
 * @author Roman Batygin
 */
public class SpecialCharacterRule extends AbstractPasswordRule {

    public SpecialCharacterRule() {
        super(PasswordRuleType.SPECIAL_CHARACTER);
    }

    @Override
    public <T> T handle(PasswordRuleVisitor<T> passwordRuleVisitor) {
        return passwordRuleVisitor.handle(this);
    }
}
