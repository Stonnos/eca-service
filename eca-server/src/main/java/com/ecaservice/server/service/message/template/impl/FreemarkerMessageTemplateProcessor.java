package com.ecaservice.server.service.message.template.impl;

import com.ecaservice.server.exception.MessageTemplateProcessingException;
import com.ecaservice.server.service.message.template.MessageTemplateProcessor;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

/**
 * Message template processing using Freemarker library.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class FreemarkerMessageTemplateProcessor implements MessageTemplateProcessor {

    private final Configuration configuration;

    /**
     * Constructor with spring dependency injection.
     *
     * @param configuration - freemarker configuration
     */
    public FreemarkerMessageTemplateProcessor(
            @Qualifier("messageTemplateFreeMarkerConfigurationFactoryBean") Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String process(String templateCode, Map<String, Object> variables) {
        log.debug("Starting to process message template [{}] with variables: {}", templateCode, variables);
        try {
            Template template = configuration.getTemplate(templateCode);
            String message = processTemplateIntoString(template, variables);
            log.debug("Message [{}] for template [{}] has been processed", message, templateCode);
            return message;
        } catch (IOException | TemplateException ex) {
            log.error("There was an error while template [{}] processing: {}", templateCode, ex.getMessage());
            throw new MessageTemplateProcessingException(ex.getMessage());
        }
    }
}
