package com.ecaservice.oauth.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Min length rule.
 *
 * @author Roman Batygin
 */
@Getter
@Setter
public class MinLengthRuleModel extends AbstractPasswordRule {

    /**
     * Min. length value
     */
    private int minLength;

    public MinLengthRuleModel() {
        super(PasswordRuleType.MIN_LENGTH);
    }

    @Override
    public <T> T handle(PasswordRuleVisitor<T> passwordRuleVisitor) {
        return passwordRuleVisitor.handle(this);
    }
}
