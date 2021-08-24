package com.ecaservice.test.common.config;

import com.ecaservice.test.common.service.TestDataProvider;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Eca common tests auto configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@ComponentScan(basePackageClasses = TestDataProvider.class)
public class EcaTestCommonAutoConfiguration {
}
