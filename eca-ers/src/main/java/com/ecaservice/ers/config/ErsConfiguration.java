package com.ecaservice.ers.config;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.config.swagger.annotation.EnableOpenApi;
import com.ecaservice.ers.model.EvaluationResultsInfo;
import com.ecaservice.ers.repository.EvaluationResultsInfoRepository;
import com.ecaservice.oauth2.annotation.Oauth2ResourceServer;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Ers configuration class.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableOpenApi
@EnableGlobalExceptionHandler
@EnableCaching
@Oauth2ResourceServer
@EntityScan(basePackageClasses = EvaluationResultsInfo.class)
@EnableJpaRepositories(basePackageClasses = EvaluationResultsInfoRepository.class)
@EnableConfigurationProperties(ErsConfig.class)
public class ErsConfiguration {
}
