package com.ecaservice.server.config.freemarker;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import java.util.Properties;

import static freemarker.template.Configuration.LOCALIZED_LOOKUP_KEY;

/**
 * Freemarker configuration class for message templates.
 *
 * @author Roman Batygin.
 */
@Configuration
@RequiredArgsConstructor
public class MessageTemplateFreemarkerConfiguration {

    /**
     * Creates freemarker configuration bean.
     *
     * @param messageTemplateLoader - message template loader
     * @return freemarker configuration bean
     */
    @Bean
    public FreeMarkerConfigurationFactoryBean messageTemplateFreeMarkerConfigurationFactoryBean(
            MessageTemplateLoader messageTemplateLoader) {
        var freeMarkerConfigurationFactoryBean = new FreeMarkerConfigurationFactoryBean();
        Properties properties = new Properties();
        properties.put(LOCALIZED_LOOKUP_KEY, Boolean.FALSE.toString());
        freeMarkerConfigurationFactoryBean.setFreemarkerSettings(properties);
        freeMarkerConfigurationFactoryBean.setPreTemplateLoaders(messageTemplateLoader);
        return freeMarkerConfigurationFactoryBean;
    }
}
