package com.ecaservice.config.db;

import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Eca datasource configuration.
 *
 * @author Roman Batygin
 */
@Configuration
public class EcaDatabaseConfiguration {

    /**
     * Creates eca datasource bean.
     *
     * @return eca datasource bean
     */
    @Primary
    @Bean
    @LiquibaseDataSource
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource ecaDatasource() {
        return DataSourceBuilder.create().build();
    }
}
