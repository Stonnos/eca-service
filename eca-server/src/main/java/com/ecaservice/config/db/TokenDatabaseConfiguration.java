package com.ecaservice.config.db;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Token datasource configuration.
 *
 * @author Roman Batygin
 */
@Configuration
public class TokenDatabaseConfiguration {

    /**
     * Creates oauth datasource bean.
     *
     * @return oauth datasource bean
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.tokendatasource")
    public DataSource tokenDataSource() {
        return DataSourceBuilder.create().build();
    }
}
