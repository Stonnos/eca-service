package com.ecaservice.oauth.model;

/**
 * Upper case character rule.
 *
 * @author Roman Batygin
 */
public class UpperCaseCharacterRule extends AbstractPasswordRule {

    public UpperCaseCharacterRule() {
        super(PasswordRuleType.UPPER_CASE_CHARACTER);
    }

    @Override
    public <T> T handle(PasswordRuleVisitor<T> passwordRuleVisitor) {
        return passwordRuleVisitor.handle(this);
    }
}
