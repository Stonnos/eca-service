package com.ecaservice.auto.test.config.db;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * Abstract datasource configuration.
 * @author Roman Batygin
 */
public abstract class AbstractDataSourceConfiguration {

    /**
     * Creates datasource bean.
     *
     * @return datasource bean
     */
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Creates entity manager factory bean.
     *
     * @param builder    - entity manager factory builder
     * @param dataSource - data source
     * @return entity manager factory bean
     */
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                       DataSource dataSource) {
        return builder.dataSource(dataSource)
                .packages(getEntityPackage())
                .build();
    }

    /**
     * Creates transaction manager bean
     *
     * @param entityManagerFactory - entity manager factory
     * @return transaction manager bean
     */
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    public abstract String getEntityPackage();
}
