package com.ecaservice.auto.test.config.db;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract datasource configuration.
 *
 * @author Roman Batygin
 */
@Getter
@Setter
public abstract class AbstractDataSourceConfiguration {

    private static final String HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";

    private String databasePlatform;
    private String ddlAuto;

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
     * @param dataSource - data source
     * @return entity manager factory bean
     */
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        var entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource);
        entityManagerFactory.setPackagesToScan(getEntityPackage());
        entityManagerFactory.setJpaPropertyMap(jpaProperties());
        entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter());
        return entityManagerFactory;
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

    /**
     * Gets entities package name.
     *
     * @return entities package name
     */
    public abstract String getEntityPackage();

    /**
     * Creates jpa properties map
     *
     * @return jpa properties map
     */
    private Map<String, Object> jpaProperties() {
        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put(HIBERNATE_HBM2DDL_AUTO, ddlAuto);
        return jpaProperties;
    }

    /**
     * Creates jpa vendor adapter.
     *
     * @return jpa vendor adapter
     */
    private JpaVendorAdapter jpaVendorAdapter() {
        var jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabasePlatform(databasePlatform);
        return jpaVendorAdapter;
    }
}
