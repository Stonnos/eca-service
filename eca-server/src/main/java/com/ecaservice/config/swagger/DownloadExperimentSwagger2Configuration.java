package com.ecaservice.config.swagger;

import com.ecaservice.controller.download.ExperimentDownloadController;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.inject.Inject;

/**
 * Swagger configuration for experiment download API.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableSwagger2
@EnableConfigurationProperties(Swagger2ApiConfig.class)
public class DownloadExperimentSwagger2Configuration extends AbstractSwagger2Configuration {

    private static final String EXPERIMENT_DOWNLOAD_GROUP = "experiment-download";

    /**
     * Constructor with spring dependency injection.
     *
     * @param swagger2ApiConfig - swagger api config bean
     */
    @Inject
    public DownloadExperimentSwagger2Configuration(Swagger2ApiConfig swagger2ApiConfig) {
        super(swagger2ApiConfig);
    }

    @Override
    @Bean(value = "downloadExperimentDocket")
    public Docket docket(TypeResolver typeResolver) {
        return super.docket(typeResolver);
    }

    @Override
    protected String getGroupName() {
        return EXPERIMENT_DOWNLOAD_GROUP;
    }

    @Override
    protected String getControllersPackage() {
        return ExperimentDownloadController.class.getPackage().getName();
    }
}
