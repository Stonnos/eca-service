package com.ecaservice.core.audit.service.template.impl;

import com.ecaservice.core.audit.entity.EventType;
import com.ecaservice.core.audit.exception.AuditTemplateProcessingException;
import com.ecaservice.core.audit.service.template.AuditTemplateProcessorService;
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
 * Audit template processing using Freemarker library.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class FreemarkerAuditTemplateProcessorService implements AuditTemplateProcessorService {

    private static final String TEMPLATE_CODE_FORMAT = "%s#%s";

    private final Configuration configuration;

    /**
     * Constructor with spring dependency injection.
     *
     * @param configuration - freemarker configuration
     */
    public FreemarkerAuditTemplateProcessorService(
            @Qualifier("auditFreeMarkerConfiguration") Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String process(String auditCode, EventType eventType, Map<String, String> variables) {
        log.debug("Starting to process template for audit code [{}], event type [{}], variables: {}", auditCode,
                eventType, variables);
        String templateCode = String.format(TEMPLATE_CODE_FORMAT, auditCode, eventType);
        try {
            Template template = configuration.getTemplate(templateCode);
            String message = processTemplateIntoString(template, variables);
            log.debug("Message [{}] for template [{}] has been processed", message, templateCode);
            return message;
        } catch (IOException | TemplateException ex) {
            log.error("There was an error while template [{}] processing: {}", templateCode, ex.getMessage());
            throw new AuditTemplateProcessingException(ex.getMessage());
        }
    }
}
