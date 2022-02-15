package com.ecaservice.core.redelivery.test;

import com.ecaservice.core.redelivery.converter.RequestMessageConverter;
import com.ecaservice.core.redelivery.converter.impl.JsonRequestMessageConverter;
import com.ecaservice.core.redelivery.error.DefaultExceptionStrategy;
import com.ecaservice.core.redelivery.error.ExceptionStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static com.ecaservice.core.redelivery.config.RedeliveryCoreAutoConfiguration.JSON_REQUEST_MESSAGE_CONVERTER;

@TestConfiguration
public class RedeliverTestConfiguration {

    @Bean("defaultExceptionStrategy")
    public ExceptionStrategy exceptionStrategy() {
        return new DefaultExceptionStrategy();
    }

    @Bean(JSON_REQUEST_MESSAGE_CONVERTER)
    public RequestMessageConverter requestMessageConverter() {
        return new JsonRequestMessageConverter(new ObjectMapper());
    }
}
