package com.ecaservice.auto.test.config.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * Mail properties.
 *
 * @author Roman Batygin
 */
@Validated
@Data
@ConfigurationProperties("mail")
public class MailProperties {

    /**
     * User name
     */
    @NotBlank
    private String userName;

    /**
     * Password
     */
    @NotBlank
    private String password;

    /**
     * Mail receiving enabled?
     */
    private Boolean enabled;
}
