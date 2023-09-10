package com.ecaservice.oauth.config;

import com.ecaservice.oauth.model.CharacterRuleModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Password config.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("password")
public class PasswordConfig {

    /**
     * Password length
     */
    private Integer length;

    /**
     * Password generator rules
     */
    private List<CharacterRuleModel> generatorRules;
}
