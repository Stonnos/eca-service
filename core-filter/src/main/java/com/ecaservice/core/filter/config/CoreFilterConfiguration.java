package com.ecaservice.core.filter.config;

import com.ecaservice.core.filter.cache.PageRequestKeyGenerator;
import com.ecaservice.core.filter.entity.FilterTemplate;
import com.ecaservice.core.filter.repository.FilterTemplateRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Core filter configuration class.
 *
 * @author Roman Batygin
 */
@Configuration
@ComponentScan({"com.ecaservice.core.filter"})
@EntityScan(basePackageClasses = FilterTemplate.class)
@EnableJpaRepositories(basePackageClasses = FilterTemplateRepository.class)
public class CoreFilterConfiguration {

    /**
     * Page request cache key generator
     */
    public static final String PAGE_REQUEST_KEY_GENERATOR = "pageRequestKeyGenerator";

    /**
     * Creates page request cache key generator.
     *
     * @return page request cache key generator
     */
    @Bean(PAGE_REQUEST_KEY_GENERATOR)
    @ConditionalOnMissingBean
    public PageRequestKeyGenerator pageRequestKeyGenerator() {
        return new PageRequestKeyGenerator();
    }
}
