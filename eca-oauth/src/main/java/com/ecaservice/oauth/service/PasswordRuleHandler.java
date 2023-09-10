package com.ecaservice.oauth.service;

import com.ecaservice.oauth.model.CharacterRuleModel;
import com.ecaservice.oauth.model.MinLengthRuleModel;
import com.ecaservice.oauth.model.PasswordRuleVisitor;
import com.ecaservice.oauth.model.RepeatCharactersRuleModel;
import org.passay.CharacterRule;
import org.passay.LengthRule;
import org.passay.RepeatCharactersRule;
import org.passay.Rule;
import org.springframework.stereotype.Service;

/**
 * Password rule handler.
 *
 * @author Roman Batygin
 */
@Service
public class PasswordRuleHandler implements PasswordRuleVisitor<Rule> {

    @Override
    public Rule handle(MinLengthRuleModel minLengthRuleModel) {
        return new LengthRule(minLengthRuleModel.getMinLength(), Integer.MAX_VALUE);
    }

    @Override
    public Rule handle(CharacterRuleModel characterRuleModel) {
        return new CharacterRule(characterRuleModel.getCharacterData());
    }

    @Override
    public Rule handle(RepeatCharactersRuleModel repeatCharactersRuleModel) {
        return new RepeatCharactersRule(repeatCharactersRuleModel.getSequenceLength());
    }
}
