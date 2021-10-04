package com.ecaservice.core.audit.config.freemarker;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import java.util.Properties;

import static freemarker.core.Configurable.NUMBER_FORMAT_KEY;
import static freemarker.template.Configuration.LOCALIZED_LOOKUP_KEY;

/**
 * Freemarker configuration class for audit.
 *
 * @author Roman Batygin
 */
@Configuration
@RequiredArgsConstructor
public class AuditFreemarkerConfiguration {

    private static final String COMPUTER_NUMBER_FORMAT = "computer";

    private final DatabaseTemplateLoader databaseTemplateLoader;

    /**
     * Creates freemarker configuration bean for audit.
     *
     * @return freemarker configuration bean
     */
    @Bean
    public FreeMarkerConfigurationFactoryBean auditFreeMarkerConfiguration() {
        var freeMarkerConfigurationFactoryBean = new FreeMarkerConfigurationFactoryBean();
        var properties = new Properties();
        properties.put(LOCALIZED_LOOKUP_KEY, Boolean.FALSE.toString());
        properties.put(NUMBER_FORMAT_KEY, COMPUTER_NUMBER_FORMAT);
        freeMarkerConfigurationFactoryBean.setFreemarkerSettings(properties);
        freeMarkerConfigurationFactoryBean.setPreTemplateLoaders(databaseTemplateLoader);
        return freeMarkerConfigurationFactoryBean;
    }
}
