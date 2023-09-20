package com.ecaservice.oauth.model;

import lombok.Builder;
import lombok.Data;

/**
 * Rule result details.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class RuleResultDetails {

    /**
     * Rule type
     */
    private PasswordRuleType ruleType;

    /**
     * Is password rule valid?
     */
    private boolean valid;

    /**
     * Message string
     */
    private String message;
}
