package com.ecaservice.server.config;

import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Eca server database configuration.
 *
 * @author Roman Batygin
 */
@Configuration
public class EcaServerDatabaseConfiguration {

    /**
     * Creates camunda datasource bean.
     *
     * @return camunda datasource bean
     */
    @Primary
    @Bean
    @LiquibaseDataSource
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource datasource() {
        return DataSourceBuilder.create().build();
    }
}
