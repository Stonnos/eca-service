package com.ecaservice.oauth.service;

import com.ecaservice.common.web.resource.JsonResourceLoader;
import com.ecaservice.oauth.model.AbstractPasswordRule;
import com.ecaservice.oauth.model.PasswordValidationResult;
import com.ecaservice.oauth.model.RuleResultDetails;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.passay.PasswordData;
import org.passay.Rule;
import org.passay.RuleResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Password validation service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordValidationService {

    private static final String VALIDATION_RULES_JSON = "password-validation-rules.json";

    private final PasswordRuleHandler passwordRuleHandler;
    private final JsonResourceLoader jsonResourceLoader = new JsonResourceLoader();

    private List<AbstractPasswordRule> rules;

    /**
     * Initialize rules.
     */
    @PostConstruct
    public void initializeRules() throws IOException {
        rules = jsonResourceLoader.load(VALIDATION_RULES_JSON, new TypeReference<>() {
        });
    }

    /**
     * Validates password with specified rules.
     *
     * @param password - password
     * @return validation result
     */
    public PasswordValidationResult validate(String password) {
        PasswordValidationResult passwordValidationResult = new PasswordValidationResult();
        passwordValidationResult.setValid(true);
        passwordValidationResult.setDetails(newArrayList());
        PasswordData passwordData = new PasswordData(password);
        for (var passwordRule : rules) {
            Rule rule = passwordRule.handle(passwordRuleHandler);
            RuleResult result = rule.validate(passwordData);
            if (!result.isValid()) {
                passwordValidationResult.setValid(false);
            }
            RuleResultDetails ruleResultDetails = RuleResultDetails.builder()
                    .ruleType(passwordRule.getRuleType())
                    .valid(result.isValid())
                    .message(passwordRule.getMessage())
                    .build();
            passwordValidationResult.getDetails().add(ruleResultDetails);
        }
        return passwordValidationResult;
    }
}
