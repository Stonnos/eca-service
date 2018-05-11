package com.ecaservice.config;

import com.ecaservice.dto.evaluation.EvaluationResultsRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.util.concurrent.Executor;

/**
 * Web - service configuration.
 *
 * @author Roman Batygin
 */
@Configuration
public class WebServiceConfiguration {

    /**
     * Creates evaluation results service config bean.
     *
     * @return evaluation results service config bea
     */
    @Bean
    public EvaluationResultsServiceConfig evaluationResultsServiceConfig() {
        return new EvaluationResultsServiceConfig();
    }

    /**
     * Creates evaluation results thread pool task executor bean.
     *
     * @param evaluationResultsServiceConfig - evaluation results service config
     * @return evaluation results thread pool task executor
     */
    @Bean
    public Executor evaluationResultsThreadPoolTaskExecutor(
            EvaluationResultsServiceConfig evaluationResultsServiceConfig) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(evaluationResultsServiceConfig.getThreadPoolSize());
        executor.setMaxPoolSize(evaluationResultsServiceConfig.getThreadPoolSize());
        return executor;
    }

    /**
     * Creates Jax2Marshaller bean.
     *
     * @return Jaxb2Marshaller bean
     */
    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(EvaluationResultsRequest.class.getPackage().getName());
        return marshaller;
    }

    /**
     * Creates web service template bean.
     *
     * @param marshaller Jaxb2Marshaller bean
     * @return web service template bean
     */
    @Bean
    public WebServiceTemplate webServiceTemplate(Jaxb2Marshaller marshaller) {
        return new WebServiceTemplate(marshaller, marshaller);
    }
}
