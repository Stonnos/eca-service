package com.ecaservice.server.config.metrics;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metrics configuration class.
 *
 * @author Roman Batygin
 */
@Configuration
public class MetricsConfiguration {

    /**
     * Creates timed aspect bean.
     *
     * @param meterRegistry - meter registry
     * @return timed aspect bean
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry meterRegistry) {
        return new TimedAspect(meterRegistry);
    }
}
