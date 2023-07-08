package com.ecaservice.data.storage.config;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.config.swagger.annotation.EnableOpenApi;
import com.ecaservice.core.filter.error.FilterExceptionHandler;
import com.ecaservice.core.lock.annotation.EnableLocks;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.oauth2.annotation.Oauth2ResourceServer;
import eca.data.file.FileDataLoader;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;

import static org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST;

/**
 * Eca data storage configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableOpenApi
@Oauth2ResourceServer
@EnableCaching
@EnableLocks
@EntityScan(basePackageClasses = InstancesEntity.class)
@EnableJpaRepositories(basePackageClasses = InstancesRepository.class)
@EnableGlobalExceptionHandler
@EnableConfigurationProperties(EcaDsConfig.class)
@Import(FilterExceptionHandler.class)
public class EcaDataStorageConfiguration {

    /**
     * Creates random string value generator bean.
     *
     * @return random string value generator bean
     */
    @Bean
    public RandomValueStringGenerator randomValueStringGenerator() {
        return new RandomValueStringGenerator();
    }

    /**
     * Creates file data loader bean.
     *
     * @param ecaDsConfig - eca ds config
     * @return file data loader bean
     */
    @Bean
    @Scope(value = SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public FileDataLoader fileDataLoader(EcaDsConfig ecaDsConfig) {
        FileDataLoader dataLoader = new FileDataLoader();
        dataLoader.setDateFormat(ecaDsConfig.getDateFormat());
        return dataLoader;
    }
}
