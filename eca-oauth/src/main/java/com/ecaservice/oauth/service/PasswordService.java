package com.ecaservice.oauth.service;

import com.ecaservice.oauth.config.PasswordConfig;
import lombok.RequiredArgsConstructor;
import org.passay.CharacterRule;
import org.passay.PasswordGenerator;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

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

    private List<CharacterRule> rules;

    /**
     * Initialize rules.
     */
    @PostConstruct
    public void initializeRules() {
        this.rules = passwordConfig.getGeneratorRules()
                .stream()
                .map(rule -> new CharacterRule(rule.getCharacterData()))
                .collect(Collectors.toList());
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
