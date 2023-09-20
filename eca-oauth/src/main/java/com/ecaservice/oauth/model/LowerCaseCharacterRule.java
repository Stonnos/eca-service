package com.ecaservice.oauth.model;

/**
 * Lower case character rule.
 *
 * @author Roman Batygin
 */
public class LowerCaseCharacterRule extends AbstractPasswordRule {

    public LowerCaseCharacterRule() {
        super(PasswordRuleType.LOWER_CASE_CHARACTER);
    }

    @Override
    public <T> T handle(PasswordRuleVisitor<T> passwordRuleVisitor) {
        return passwordRuleVisitor.handle(this);
    }
}
