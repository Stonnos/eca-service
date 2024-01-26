package com.ecaservice.user.profile.options.client.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * User profile options client library auto configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@ComponentScan({"com.ecaservice.user.profile.options.client"})
public class UserProfileOptionsClientAutoConfiguration {
}
