package com.ecaservice.oauth.service;

import com.ecaservice.oauth.config.PasswordConfig;
import lombok.RequiredArgsConstructor;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Password service.
 *
 * @author Roman Batygin
 */
@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordGenerator passwordGenerator;
    private final PasswordConfig passwordConfig;

    /**
     * Generates random password.
     *
     * @return random password
     */
    public String generatePassword() {
        List<CharacterRule> rules = newArrayList();
        addRuleIfSpecified(rules, passwordConfig.getUseDigits(), EnglishCharacterData.Digit);
        addRuleIfSpecified(rules, passwordConfig.getUseLowerCaseSymbols(), EnglishCharacterData.LowerCase);
        addRuleIfSpecified(rules, passwordConfig.getUseUpperCaseSymbols(), EnglishCharacterData.UpperCase);
        Assert.notEmpty(rules, "Password generator rules must be not empty! Specified at least one rule in configs");
        return passwordGenerator.generatePassword(passwordConfig.getLength(), rules);
    }

    private void addRuleIfSpecified(List<CharacterRule> rules, Boolean specified, EnglishCharacterData characterData) {
        if (Boolean.TRUE.equals(specified)) {
            rules.add(new CharacterRule(characterData));
        }
    }
}
