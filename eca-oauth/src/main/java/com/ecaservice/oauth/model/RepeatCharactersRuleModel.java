package com.ecaservice.oauth.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Repeat characters rule.
 *
 * @author Roman Batygin
 */
@Getter
@Setter
public class RepeatCharactersRuleModel extends AbstractPasswordRule {

    /**
     * Repeat characters sequence length
     */
    private int sequenceLength;

    public RepeatCharactersRuleModel() {
        super(PasswordRuleType.REPEAT_CHARACTERS);
    }

    @Override
    public <T> T handle(PasswordRuleVisitor<T> passwordRuleVisitor) {
        return passwordRuleVisitor.handle(this);
    }
}
