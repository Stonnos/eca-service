package com.ecaservice.data.storage.config.db;

import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Eca ds datasource configuration.
 *
 * @author Roman Batygin
 */
@Configuration
public class EcaDsDatabaseConfiguration {

    /**
     * Creates eca ds datasource bean.
     *
     * @return eca ds datasource bean
     */
    @Primary
    @Bean
    @LiquibaseDataSource
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource ecaDsDatasource() {
        return DataSourceBuilder.create().build();
    }
}
