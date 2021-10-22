package com.ecaservice.feign.metrics.config;

import feign.Feign;
import feign.micrometer.MicrometerCapability;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Feign client metric auto configuration.
 *
 * @author Roman Batygin
 */
@Configuration
public class FeignClientMetricsAutoConfiguration {

    /**
     * Adds micrometer capability for feign clients.
     *
     * @param meterRegistry - meter registry
     * @return feign builder bean
     */
    @Bean
    @Scope("prototype")
    public Feign.Builder feignBuilder(MeterRegistry meterRegistry) {
        return Feign.builder()
                .addCapability(new MicrometerCapability(meterRegistry));
    }
}
