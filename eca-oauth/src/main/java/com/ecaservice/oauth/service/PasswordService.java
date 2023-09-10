package com.ecaservice.oauth.service;

import com.ecaservice.oauth.config.PasswordConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Password service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordGenerator passwordGenerator;
    private final PasswordConfig passwordConfig;

    private final List<CharacterRule> rules = newArrayList();

    /**
     * Initialize rules.
     */
    @PostConstruct
    public void initializeRules() {
        this.rules.add(new CharacterRule(EnglishCharacterData.Digit));
        this.rules.add(new CharacterRule(EnglishCharacterData.UpperCase));
        this.rules.add(new CharacterRule(EnglishCharacterData.LowerCase));
    }

    /**
     * Generates random password.
     *
     * @return random password
     */
    public String generatePassword() {
        return passwordGenerator.generatePassword(passwordConfig.getLength(), rules);
    }
}
