package com.ecaservice.config;

import eca.data.file.FileDataLoader;
import eca.data.file.FileDataSaver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Eca - service configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableScheduling
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
     * Creates <tt>MailConfig</tt> bean
     *
     * @return {@link MailConfig} bean
     */
    @Bean
    public MailConfig mailConfig() {
        return new MailConfig();
    }

    /**
     * Creates <tt>FileDataSaver</tt> bean
     *
     * @param experimentConfig {@link ExperimentConfig} bean
     * @return {@link FileDataSaver} bean
     */
    @Bean
    public FileDataSaver dataSaver(ExperimentConfig experimentConfig) {
        FileDataSaver dataSaver = new FileDataSaver();
        dataSaver.setDateFormat(experimentConfig.getData().getDateFormat());
        return dataSaver;
    }

    /**
     * Creates <tt>FileDataLoader</tt> bean
     *
     * @param experimentConfig {@link ExperimentConfig} bean
     * @return {@link FileDataLoader} bean
     */
    @Bean
    public FileDataLoader dataLoader(ExperimentConfig experimentConfig) {
        FileDataLoader dataLoader = new FileDataLoader();
        dataLoader.setDateFormat(experimentConfig.getData().getDateFormat());
        return dataLoader;
    }

}
