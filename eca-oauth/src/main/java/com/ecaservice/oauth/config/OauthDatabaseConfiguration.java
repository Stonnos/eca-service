package com.ecaservice.oauth.config;

import com.ecaservice.oauth.model.entity.UserEntity;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.inject.Inject;
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

    private static final String SPRING_DATASOURCE_DRIVER_CLASS_NAME = "spring.datasource.driver-class-name";
    private static final String SPRING_DATASOURCE_URL = "spring.datasource.url";
    private static final String SPRING_DATASOURCE_USERNAME = "spring.datasource.username";
    private static final String SPRING_DATASOURCE_PASSWORD = "spring.datasource.password";

    private final Environment environment;

    /**
     * Constructor with spring dependency injection.
     *
     * @param environment - environment bean
     */
    @Inject
    public OauthDatabaseConfiguration(Environment environment) {
        this.environment = environment;
    }

    /**
     * Creates datasource bean.
     *
     * @return datasource bean
     */
    @Primary
    @Bean
    public DataSource oauthDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getProperty(SPRING_DATASOURCE_DRIVER_CLASS_NAME));
        dataSource.setUrl(environment.getProperty(SPRING_DATASOURCE_URL));
        dataSource.setUsername(environment.getProperty(SPRING_DATASOURCE_USERNAME));
        dataSource.setPassword(environment.getProperty(SPRING_DATASOURCE_PASSWORD));
        return dataSource;
    }

    /**
     * Creates entity manager factory bean.
     *
     * @param builder         - entity manager factory builder
     * @param oauthDataSource - data source
     * @return entity manager factory bean
     */
    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean oauthEntityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                            DataSource oauthDataSource) {
        return builder.dataSource(oauthDataSource).packages(UserEntity.class.getPackage().getName()).build();
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
