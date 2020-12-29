package com.ecaservice.external.api.test.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Camunda database configuration.
 *
 * @author Roman Batygin
 */
@Configuration
public class CamundaDatabaseConfiguration {

    /**
     * Creates camunda datasource bean.
     *
     * @return camunda datasource bean
     */
    @Bean(name = "camundaBpmDataSource")
    @ConfigurationProperties(prefix = "spring.camunda")
    public DataSource datasource() {
        return DataSourceBuilder.create().build();
    }
}
