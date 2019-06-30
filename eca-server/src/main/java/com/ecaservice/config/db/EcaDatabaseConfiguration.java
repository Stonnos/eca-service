package com.ecaservice.config.db;

import com.ecaservice.model.entity.EvaluationLog;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * Eca datasource configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "ecaEntityManagerFactory",
        transactionManagerRef = "ecaTransactionManager",
        basePackages = {"com.ecaservice.repository"}
)
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

    /**
     * Creates entity manager factory bean.
     *
     * @param builder       - entity manager factory builder
     * @param ecaDatasource - eca datasource
     * @return entity manager factory bean
     */
    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean ecaEntityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                          DataSource ecaDatasource) {
        return builder.dataSource(ecaDatasource).packages(EvaluationLog.class.getPackage().getName()).build();
    }

    /**
     * Creates transaction manager bean
     *
     * @param ecaEntityManagerFactory - entity manager factory
     * @return transaction manager bean
     */
    @Primary
    @Bean
    public PlatformTransactionManager ecaTransactionManager(EntityManagerFactory ecaEntityManagerFactory) {
        return new JpaTransactionManager(ecaEntityManagerFactory);
    }
}
