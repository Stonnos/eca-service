package com.ecaservice.feign.metrics.config;

import feign.Capability;
import feign.micrometer.MicrometerCapability;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign metrics auto configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "spring.cloud.openfeign.micrometer.enabled", havingValue = "true", matchIfMissing = true)
public class FeignMetricsAutoConfiguration {

    /**
     * Creates micrometer capability bean.
     *
     * @param registry - meter registry
     * @return micrometer capability bean
     */
    @Bean
    public Capability micrometerCapability(MeterRegistry registry) {
        log.info("Feign client micrometer capability has been configured");
        return new MicrometerCapability(registry);
    }
}