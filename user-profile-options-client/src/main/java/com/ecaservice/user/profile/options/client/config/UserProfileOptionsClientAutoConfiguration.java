package com.ecaservice.user.profile.options.client.config;

import com.ecaservice.user.profile.options.client.service.UserProfileOptionsFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * User profile options client library auto configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@EnableFeignClients(basePackageClasses = UserProfileOptionsFeignClient.class)
@ComponentScan({"com.ecaservice.user.profile.options.client"})
public class UserProfileOptionsClientAutoConfiguration {
}
