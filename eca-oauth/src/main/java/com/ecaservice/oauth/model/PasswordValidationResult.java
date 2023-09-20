package com.ecaservice.oauth.model;

import lombok.Data;

import java.util.List;

/**
 * Password validation result.
 *
 * @author Roman Batygin
 */
@Data
public class PasswordValidationResult {

    /**
     * Is password valid?
     */
    private boolean valid;

    /**
     * Password rules details
     */
    private List<RuleResultDetails> details;
}
