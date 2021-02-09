package com.ecaservice.mail.service.template.impl;

import com.ecaservice.mail.exception.TemplateProcessingException;
import com.ecaservice.mail.service.template.TemplateProcessorService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

/**
 * Template processing using Freemarker library.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FreemarkerTemplateProcessorService implements TemplateProcessorService {

    private final Configuration configuration;

    @Override
    public String process(String templateCode, Map<String, String> variables) {
        log.debug("Starting to process template [{}] with variables: {}", templateCode, variables);
        try {
            Template template = configuration.getTemplate(templateCode);
            String message = processTemplateIntoString(template, variables);
            log.debug("Message [{}] for template [{}] has been processed", message, templateCode);
            return message;
        } catch (IOException | TemplateException ex) {
            log.error("There was an error while template [{}] processing: {}", templateCode, ex.getMessage());
            throw new TemplateProcessingException(ex.getMessage());
        }
    }
}
