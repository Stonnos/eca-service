package com.ecaservice.config;

import eca.data.file.FileDataLoader;
import eca.data.file.FileDataSaver;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
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
@EnableCaching
@EnableAsync
public class EcaServiceConfiguration {

    /**
     * Creates executor service bean.
     *
     * @return executor service bean
     */
    @Bean
    public ExecutorService executorService() {
        return Executors.newCachedThreadPool();
    }

    /**
     * Creates cross - validation config bean.
     *
     * @return cross - validation config bean
     */
    @Bean
    public CrossValidationConfig crossValidationConfig() {
        return new CrossValidationConfig();
    }

    /**
     * Creates experiment config bean.
     *
     * @return experiment config bean
     */
    @Bean
    public ExperimentConfig experimentConfig() {
        return new ExperimentConfig();
    }

    /**
     * Creates mail config bean.
     *
     * @return mail config bean
     */
    @Bean
    public MailConfig mailConfig() {
        return new MailConfig();
    }

    /**
     * Creates file data saver bean.
     *
     * @param experimentConfig experiment config bean
     * @return file data saver  bean
     */
    @Bean
    public FileDataSaver dataSaver(ExperimentConfig experimentConfig) {
        FileDataSaver dataSaver = new FileDataSaver();
        dataSaver.setDateFormat(experimentConfig.getData().getDateFormat());
        return dataSaver;
    }

    /**
     * Creates file data loader bean.
     *
     * @param experimentConfig experiment config bean
     * @return file data loader bean
     */
    @Bean
    public FileDataLoader dataLoader(ExperimentConfig experimentConfig) {
        FileDataLoader dataLoader = new FileDataLoader();
        dataLoader.setDateFormat(experimentConfig.getData().getDateFormat());
        return dataLoader;
    }

}
