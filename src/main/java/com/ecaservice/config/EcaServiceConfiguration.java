package com.ecaservice.config;

import eca.data.DataLoader;
import eca.data.DataSaver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Eca - service configuration.
 *
 * @author Roman Batygin
 */
@Configuration
public class EcaServiceConfiguration {

    /**
     * Creates <tt>ExecutorService</tt> bean
     *
     * @return {@link ExecutorService} bean
     */
    @Bean
    public ExecutorService executorService() {
        return Executors.newCachedThreadPool();
    }

    /**
     * Creates <tt>CrossValidationConfig</tt> bean
     *
     * @return {@link CrossValidationConfig} bean
     */
    @Bean
    public CrossValidationConfig crossValidationConfig() {
        return new CrossValidationConfig();
    }

    /**
     * Creates <tt>ExperimentConfig</tt> bean
     *
     * @return {@link ExperimentConfig} bean
     */
    @Bean
    public ExperimentConfig experimentConfig() {
        return new ExperimentConfig();
    }

    /**
     * Creates <tt>DataSaver</tt> bean
     *
     * @return {@link DataSaver} bean
     */
    @Bean
    public DataSaver dataSaver() {
        ExperimentConfig experimentConfig = experimentConfig();
        DataSaver dataSaver = new DataSaver();
        dataSaver.setDateFormat(experimentConfig.getDateFormat());
        return dataSaver;
    }

    /**
     * Creates <tt>DataLoader</tt> bean
     *
     * @return {@link DataLoader} bean
     */
    @Bean
    public DataLoader dataLoader() {
        ExperimentConfig experimentConfig = experimentConfig();
        DataLoader dataLoader = new DataLoader();
        dataLoader.setDateFormat(experimentConfig.getDateFormat());
        return dataLoader;
    }
}
