package com.ecaservice.oauth.model;

import lombok.Getter;
import lombok.Setter;
import org.passay.EnglishCharacterData;

/**
 * Character rule.
 *
 * @author Roman Batygin
 */
@Getter
@Setter
public class CharacterRuleModel extends AbstractPasswordRule {

    /**
     * Character data
     */
    private EnglishCharacterData characterData;

    public CharacterRuleModel() {
        super(PasswordRuleType.CHARACTER);
    }

    @Override
    public <T> T handle(PasswordRuleVisitor<T> passwordRuleVisitor) {
        return passwordRuleVisitor.handle(this);
    }
}
