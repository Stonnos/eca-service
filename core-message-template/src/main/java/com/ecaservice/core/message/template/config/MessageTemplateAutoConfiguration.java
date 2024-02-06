package com.ecaservice.core.message.template.config;

import com.ecaservice.core.message.template.entity.MessageTemplateEntity;
import com.ecaservice.core.message.template.repository.MessageTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import java.util.Properties;

import static freemarker.template.Configuration.LOCALIZED_LOOKUP_KEY;

/**
 * Freemarker configuration class for message templates.
 *
 * @author Roman Batygin.
 */
@Configuration
@ComponentScan({"com.ecaservice.core.message.template"})
@EntityScan(basePackageClasses = MessageTemplateEntity.class)
@EnableJpaRepositories(basePackageClasses = MessageTemplateRepository.class)
@RequiredArgsConstructor
public class MessageTemplateAutoConfiguration {

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
