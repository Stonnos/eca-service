package com.ecaservice.configuation;

import com.ecaservice.service.evaluation.CalculationExecutorService;
import com.ecaservice.service.evaluation.CalculationExecutorServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Executor service configuration.
 *
 * @author Roman Batygin
 */
@Configuration
public class ExecutorConfiguration {

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
     * Creates calculation executor service bean.
     *
     * @param executorService - executor service bean
     * @return calculation executor service bean
     */
    @Bean
    public CalculationExecutorService calculationExecutorService(ExecutorService executorService) {
        return new CalculationExecutorServiceImpl(executorService);
    }
}
