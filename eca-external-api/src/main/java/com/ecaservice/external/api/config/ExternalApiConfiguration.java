package com.ecaservice.external.api.config;

import com.ecaservice.config.swagger.annotation.EnableOpenApi;
import com.ecaservice.oauth2.annotation.Oauth2ResourceServer;
import eca.data.file.FileDataLoader;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST;

/**
 * Eca external API configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableOpenApi
@Oauth2ResourceServer
@EnableScheduling
@EnableConfigurationProperties(ExternalApiConfig.class)
public class ExternalApiConfiguration {

    /**
     * Creates file data loader bean.
     *
     * @param externalApiConfig - external api config
     * @return file data loader bean
     */
    @Bean
    @Scope(value = SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public FileDataLoader fileDataLoader(ExternalApiConfig externalApiConfig) {
        FileDataLoader dataLoader = new FileDataLoader();
        dataLoader.setDateFormat(externalApiConfig.getDateFormat());
        return dataLoader;
    }
}
