package com.ecaservice.core.data.cleaner.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Table data cleaner auto configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@EnableScheduling
@Configuration
@EnableConfigurationProperties(TableDataCleanerProperties.class)
@ComponentScan({"com.ecaservice.core.data.cleaner"})
@ConditionalOnProperty(value = "table.data.clean.enabled", havingValue = "true")
public class TableDataCleanerAutoConfiguration {
}
