package com.ecaservice.oauth.service;

import com.ecaservice.oauth.config.PasswordConfig;
import lombok.RequiredArgsConstructor;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
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

    private List<CharacterRule> rules = newArrayList();

    /**
     * Initialize password generator rules.
     */
    @PostConstruct
    public void initializeRules() {
        rules = newArrayList();
        addRuleIfSpecified(rules, passwordConfig.getUseDigits(), EnglishCharacterData.Digit);
        addRuleIfSpecified(rules, passwordConfig.getUseLowerCaseSymbols(), EnglishCharacterData.LowerCase);
        addRuleIfSpecified(rules, passwordConfig.getUseUpperCaseSymbols(), EnglishCharacterData.UpperCase);
        Assert.notEmpty(rules, "Password generator rules must be not empty! Specified at least one rule in configs");
    }

    /**
     * Generates random password.
     *
     * @return random password
     */
    public String generatePassword() {
        return passwordGenerator.generatePassword(passwordConfig.getLength(), rules);
    }

    private void addRuleIfSpecified(List<CharacterRule> rules, Boolean specified, EnglishCharacterData characterData) {
        if (Boolean.TRUE.equals(specified)) {
            rules.add(new CharacterRule(characterData));
        }
    }
}
