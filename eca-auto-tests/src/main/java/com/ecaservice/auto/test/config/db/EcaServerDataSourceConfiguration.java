package com.ecaservice.auto.test.config.db;

import com.ecaservice.auto.test.entity.ecaserver.AbstractEvaluationEntity;
import com.ecaservice.auto.test.repository.ecaserver.EvaluationLogRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * Eca server datasource configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@ConfigurationProperties(prefix = "spring.ecaserverdatasource.jpa")
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = EcaServerDataSourceConfiguration.ECA_SERVER_ENTITY_MANAGER_FACTORY,
        transactionManagerRef = EcaServerDataSourceConfiguration.ECA_SERVER_TRANSACTION_MANAGER,
        basePackageClasses = EvaluationLogRepository.class
)
public class EcaServerDataSourceConfiguration extends AbstractDataSourceConfiguration {

    public static final String ECA_SERVER_DATASOURCE = "ecaServerDatasource";
    public static final String ECA_SERVER_ENTITY_MANAGER_FACTORY = "ecaServerEntityManagerFactory";
    public static final String ECA_SERVER_TRANSACTION_MANAGER = "ecaServerTransactionManager";

    /**
     * Creates datasource bean.
     *
     * @return datasource bean
     */
    @Override
    @Bean(ECA_SERVER_DATASOURCE)
    @ConfigurationProperties(prefix = "spring.ecaserverdatasource")
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
    @Bean(ECA_SERVER_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier(ECA_SERVER_DATASOURCE) DataSource dataSource) {
        return super.entityManagerFactory(dataSource);
    }

    /**
     * Creates transaction manager bean
     *
     * @param entityManagerFactory - entity manager factory
     * @return transaction manager bean
     */
    @Override
    @Bean(ECA_SERVER_TRANSACTION_MANAGER)
    public PlatformTransactionManager transactionManager(
            @Qualifier(ECA_SERVER_ENTITY_MANAGER_FACTORY) EntityManagerFactory entityManagerFactory) {
        return super.transactionManager(entityManagerFactory);
    }

    @Override
    public String getEntityPackage() {
        return AbstractEvaluationEntity.class.getPackageName();
    }
}
