package com.ecaservice.core.tracing.config;

import com.ecaservice.core.tracing.report.NoopReporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.Span;
import zipkin2.codec.BytesEncoder;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;

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
     * Creates async span reporter bean.
     *
     * @param sender  - trace sender
     * @param encoder - span encoder
     * @return span reporter bean
     */
    @Bean
    @ConditionalOnProperty(value = "management.zipkin.tracing.enabled", havingValue = "true")
    public AsyncReporter<Span> spanReporter(Sender sender, BytesEncoder<Span> encoder) {
        log.info("Zipkin async reporter has been configured");
        return AsyncReporter.builder(sender).build(encoder);
    }

    /**
     * Creates span noop reporter bean.
     *
     * @return span noop reporter bean
     */
    @Bean
    @ConditionalOnProperty(value = "management.zipkin.tracing.enabled", havingValue = "false", matchIfMissing = true)
    public NoopReporter noopReporter() {
        return new NoopReporter();
    }
}
