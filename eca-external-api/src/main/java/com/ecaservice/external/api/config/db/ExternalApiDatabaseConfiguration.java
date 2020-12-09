package com.ecaservice.external.api.config.db;

import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Eca external API datasource configuration.
 *
 * @author Roman Batygin
 */
@Configuration
public class ExternalApiDatabaseConfiguration {

    /**
     * Creates eca external API datasource bean.
     *
     * @return eca external API datasource bean
     */
    @Primary
    @Bean
    @LiquibaseDataSource
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource ecaExternalApiDatasource() {
        return DataSourceBuilder.create().build();
    }
}
