package com.ecaservice.core.message.template.config;

import com.ecaservice.common.web.resource.JsonResourceLoader;
import com.ecaservice.core.message.template.model.MessageTemplate;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import java.util.List;
import java.util.Properties;

import static freemarker.template.Configuration.LOCALIZED_LOOKUP_KEY;

/**
 * Freemarker configuration class for message templates.
 *
 * @author Roman Batygin.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(MessageTemplateProperties.class)
@ComponentScan({"com.ecaservice.core.message.template"})
@RequiredArgsConstructor
public class MessageTemplateAutoConfiguration {

    private final JsonResourceLoader jsonResourceLoader = new JsonResourceLoader();

    /**
     * Creates message template config bean.
     *
     * @return message template config
     */
    @Bean
    @SneakyThrows
    public MessageTemplateConfig messageTemplateConfig(MessageTemplateProperties messageTemplateProperties) {
        String location = messageTemplateProperties.getLocation();
        List<MessageTemplate> templates = jsonResourceLoader.load(location, new TypeReference<>() {
        });
        MessageTemplateConfig messageTemplateConfig = new MessageTemplateConfig(templates);
        log.info("[{}] message templates has been loaded", templates.size());
        return messageTemplateConfig;
    }

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
