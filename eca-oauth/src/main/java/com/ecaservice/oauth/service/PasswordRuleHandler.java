package com.ecaservice.oauth.service;

import com.ecaservice.oauth.model.DigitRule;
import com.ecaservice.oauth.model.LowerCaseCharacterRule;
import com.ecaservice.oauth.model.MinLengthRule;
import com.ecaservice.oauth.model.PasswordRuleVisitor;
import com.ecaservice.oauth.model.RepeatCharacterRule;
import com.ecaservice.oauth.model.SpecialCharacterRule;
import com.ecaservice.oauth.model.UpperCaseCharacterRule;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
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
    public Rule handle(MinLengthRule minLengthRule) {
        return new LengthRule(minLengthRule.getMinLength(), Integer.MAX_VALUE);
    }

    @Override
    public Rule handle(SpecialCharacterRule specialCharacterRule) {
        return new CharacterRule(EnglishCharacterData.Special);
    }

    @Override
    public Rule handle(DigitRule digitRule) {
        return new CharacterRule(EnglishCharacterData.Digit);
    }

    @Override
    public Rule handle(LowerCaseCharacterRule lowerCaseCharacterRule) {
        return new CharacterRule(EnglishCharacterData.LowerCase);
    }

    @Override
    public Rule handle(UpperCaseCharacterRule upperCaseCharacterRule) {
        return new CharacterRule(EnglishCharacterData.UpperCase);
    }

    @Override
    public Rule handle(RepeatCharacterRule repeatCharacterRule) {
        return new RepeatCharactersRule(repeatCharacterRule.getSequenceLength());
    }
}
