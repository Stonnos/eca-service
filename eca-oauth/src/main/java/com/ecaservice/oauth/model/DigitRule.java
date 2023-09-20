package com.ecaservice.oauth.model;

/**
 * Digit rule.
 *
 * @author Roman Batygin
 */
public class DigitRule extends AbstractPasswordRule {

    public DigitRule() {
        super(PasswordRuleType.DIGIT);
    }

    @Override
    public <T> T handle(PasswordRuleVisitor<T> passwordRuleVisitor) {
        return passwordRuleVisitor.handle(this);
    }
}
