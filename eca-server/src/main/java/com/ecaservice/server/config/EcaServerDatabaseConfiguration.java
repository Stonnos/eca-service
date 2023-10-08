package com.ecaservice.server.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Eca server database configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
public class EcaServerDatabaseConfiguration {

    /**
     * Creates eca server datasource bean.
     *
     * @param maximumPoolSize - datasource maximum pool size
     * @return eca server datasource bean
     */
    @Primary
    @Bean
    @LiquibaseDataSource
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource datasource(@Value("${spring.datasource.hikari.maximum-pool-size}") int maximumPoolSize) {
        var dataSource = DataSourceBuilder.create().build();
        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        log.info("Hikari pool size [{}] has been set", maximumPoolSize);
        hikariDataSource.setMaximumPoolSize(maximumPoolSize);
        return dataSource;
    }

    /**
     * Creates transaction manager bean.
     *
     * @return transaction manager
     */
    @Primary
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager();
    }
}
