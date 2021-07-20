package com.ecaservice.load.test.config;

import com.ecaservice.classifier.options.config.ClassifiersOptionsConfiguration;
import com.ecaservice.config.swagger.annotation.EnableOpenApi;
import org.jxls.util.JxlsHelper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Eca load tests configuration class.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableOpenApi
@EnableCaching
@EnableScheduling
@EnableConfigurationProperties(EcaLoadTestsConfig.class)
@Import(ClassifiersOptionsConfiguration.class)
public class EcaLoadTestsConfiguration {

    /**
     * Creates jxls helper bean.
     *
     * @return jxls helper bean
     */
    @Bean
    public JxlsHelper jxlsHelper() {
        return JxlsHelper.getInstance();
    }
}
