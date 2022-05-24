package com.ecaservice.core.redelivery.test;

import com.ecaservice.core.redelivery.callback.RetryCallback;
import com.ecaservice.core.redelivery.callback.impl.DefaultRetryCallback;
import com.ecaservice.core.redelivery.converter.RequestMessageConverter;
import com.ecaservice.core.redelivery.converter.impl.JsonRequestMessageConverter;
import com.ecaservice.core.redelivery.error.DefaultExceptionStrategy;
import com.ecaservice.core.redelivery.error.ExceptionStrategy;
import com.ecaservice.core.redelivery.strategy.DefaultRetryStrategy;
import com.ecaservice.core.redelivery.strategy.RetryStrategy;
import com.ecaservice.core.redelivery.strategy.function.RetryDegreeFunction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static com.ecaservice.core.redelivery.config.RedeliveryCoreAutoConfiguration.DEFAULT_EXCEPTION_STRATEGY;
import static com.ecaservice.core.redelivery.config.RedeliveryCoreAutoConfiguration.DEFAULT_RETRY_CALLBACK;
import static com.ecaservice.core.redelivery.config.RedeliveryCoreAutoConfiguration.DEFAULT_RETRY_STRATEGY;
import static com.ecaservice.core.redelivery.config.RedeliveryCoreAutoConfiguration.JSON_REQUEST_MESSAGE_CONVERTER;

@TestConfiguration
public class RedeliverTestConfiguration {

    private static final long MIN_RETRY_INTERVAL_MILLIS = 30000L;
    private static final int MAX_RETRIES_IN_ROW = 10;
    private static final int MAX_RETRIES = 100;

    @Bean(DEFAULT_EXCEPTION_STRATEGY)
    public ExceptionStrategy exceptionStrategy() {
        return new DefaultExceptionStrategy();
    }

    @Bean(JSON_REQUEST_MESSAGE_CONVERTER)
    public RequestMessageConverter requestMessageConverter() {
        return new JsonRequestMessageConverter(new ObjectMapper());
    }

    @Bean(DEFAULT_RETRY_CALLBACK)
    public RetryCallback retryCallback() {
        return new DefaultRetryCallback();
    }

    @Bean(DEFAULT_RETRY_STRATEGY)
    public RetryStrategy retryStrategy() {
        var strategy = new DefaultRetryStrategy();
        strategy.setMaxRetries(MAX_RETRIES);
        strategy.setMaxRetriesInRow(MAX_RETRIES_IN_ROW);
        strategy.setMinRetryIntervalMillis(MIN_RETRY_INTERVAL_MILLIS);
        strategy.setRetryFunction(new RetryDegreeFunction());
        return strategy;
    }
}
