package com.ecaservice.server.config.camunda;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

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

    /**
     * Creates camunda transaction manager bean.
     *
     * @param dataSource - camunda datasource
     * @return transaction manager
     */
    @Bean(name = "camundaBpmTransactionManager")
    public PlatformTransactionManager camundaTransactionManager(
            @Qualifier("camundaBpmDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
