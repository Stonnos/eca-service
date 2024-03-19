package com.ecaservice.user.profile.options.cache.client.config;

import com.ecaservice.user.profile.options.cache.client.entity.UserProfileOptionsDataEntity;
import com.ecaservice.user.profile.options.cache.client.repository.UserProfileOptionsDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * User profile options cache client library auto configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(UserProfileOptionsCacheClientProperties.class)
@EntityScan(basePackageClasses = UserProfileOptionsDataEntity.class)
@EnableJpaRepositories(basePackageClasses = UserProfileOptionsDataRepository.class)
@ConditionalOnProperty(value = "user-profile.client.cache.enabled", havingValue = "true")
@ComponentScan({"com.ecaservice.user.profile.options.cache.client"})
public class UserProfileOptionsCacheClientAutoConfiguration {
}
