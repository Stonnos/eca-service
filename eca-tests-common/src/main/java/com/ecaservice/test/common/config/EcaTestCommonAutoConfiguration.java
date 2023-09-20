package com.ecaservice.test.common.config;

import com.ecaservice.test.common.service.TestDataProvider;
import com.ecaservice.test.common.service.api.DataLoaderApiClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Eca common tests auto configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableFeignClients(basePackageClasses = DataLoaderApiClient.class)
@ComponentScan(basePackageClasses = TestDataProvider.class)
public class EcaTestCommonAutoConfiguration {
}
