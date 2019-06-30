package com.ecaservice.oauth.config;

import com.ecaservice.oauth.entity.UserEntity;
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
 * Oauth datasource configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "oauthEntityManagerFactory",
        transactionManagerRef = "oauthTransactionManager",
        basePackages = {"com.ecaservice.oauth.repository"}
)
public class OauthDatabaseConfiguration {

    /**
     * Creates datasource bean.
     *
     * @return datasource bean
     */
    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource oauthDatasource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Creates entity manager factory bean.
     *
     * @param builder         - entity manager factory builder
     * @param oauthDatasource - data source
     * @return entity manager factory bean
     */
    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean oauthEntityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                            DataSource oauthDatasource) {
        return builder.dataSource(oauthDatasource).packages(UserEntity.class.getPackage().getName()).build();
    }

    /**
     * Creates transaction manager bean
     *
     * @param oauthEntityManagerFactory - entity manager factory
     * @return transaction manager bean
     */
    @Primary
    @Bean
    public PlatformTransactionManager oauthTransactionManager(EntityManagerFactory oauthEntityManagerFactory) {
        return new JpaTransactionManager(oauthEntityManagerFactory);
    }
}
