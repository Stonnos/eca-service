package com.ecaservice.external.api.test.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * External api tests database configuration.
 *
 * @author Roman Batygin
 */
@Configuration
public class ExternalApiTestsDatabaseConfiguration {

    /**
     * Creates datasource bean.
     *
     * @return datasource bean
     */
    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource datasource() {
        return DataSourceBuilder.create().build();
    }
}
