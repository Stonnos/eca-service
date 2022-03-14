package com.ecaservice.auto.test.config.db;

import com.ecaservice.auto.test.entity.autotest.BaseEntity;
import com.ecaservice.auto.test.repository.autotest.AutoTestsJobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * Auto tests datasource configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@ConfigurationProperties(prefix = "spring.datasource.jpa")
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = EcaAutoTestsDataSourceConfiguration.ECA_AUTO_TESTS_ENTITY_MANAGER_FACTORY,
        transactionManagerRef = EcaAutoTestsDataSourceConfiguration.ECA_AUTO_TESTS_TRANSACTION_MANAGER,
        basePackageClasses = AutoTestsJobRepository.class
)
public class EcaAutoTestsDataSourceConfiguration extends AbstractDataSourceConfiguration {

    public static final String ECA_AUTO_TESTS_DATASOURCE = "ecaAutoTestsDatasource";
    public static final String ECA_AUTO_TESTS_ENTITY_MANAGER_FACTORY = "ecaAutoTestsEntityManagerFactory";
    public static final String ECA_AUTO_TESTS_TRANSACTION_MANAGER = "ecaAutoTestsTransactionManager";

    /**
     * Creates datasource bean.
     *
     * @return datasource bean
     */
    @Override
    @Primary
    @Bean(ECA_AUTO_TESTS_DATASOURCE)
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return super.dataSource();
    }

    /**
     * Creates entity manager factory bean.
     *
     * @param dataSource - data source
     * @return entity manager factory bean
     */
    @Override
    @Primary
    @Bean(ECA_AUTO_TESTS_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier(ECA_AUTO_TESTS_DATASOURCE) DataSource dataSource) {
        return super.entityManagerFactory(dataSource);
    }

    /**
     * Creates transaction manager bean
     *
     * @param entityManagerFactory - entity manager factory
     * @return transaction manager bean
     */
    @Override
    @Primary
    @Bean(ECA_AUTO_TESTS_TRANSACTION_MANAGER)
    public PlatformTransactionManager transactionManager(
            @Qualifier(ECA_AUTO_TESTS_ENTITY_MANAGER_FACTORY) EntityManagerFactory entityManagerFactory) {
        return super.transactionManager(entityManagerFactory);
    }

    @Override
    public String getEntityPackage() {
        return BaseEntity.class.getPackageName();
    }
}
