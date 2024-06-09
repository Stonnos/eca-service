package com.ecaservice.core.tracing.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.Span;
import zipkin2.reporter.Reporter;

/**
 * Zipkin report configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(ZipkinReportProperties.class)
public class ZipkinReportAutoConfiguration {

    /**
     * Creates span noop reporter bean.
     *
     * @return span noop reporter bean
     */
    @Bean
    @ConditionalOnProperty(value = "management.zipkin.tracing.enabled", havingValue = "false", matchIfMissing = true)
    public Reporter<Span> noopReporter() {
        return Reporter.NOOP;
    }
}
