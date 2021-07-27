package com.ecaservice.auto.test.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
@Data
@ConfigurationProperties("mail")
public class MailProperties {

    @NotBlank
    private String userName;

    @NotBlank
    private String password;
}
