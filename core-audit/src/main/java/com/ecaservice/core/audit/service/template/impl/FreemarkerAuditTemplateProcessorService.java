package com.ecaservice.core.audit.service.template.impl;

import com.ecaservice.core.audit.entity.EventType;
import com.ecaservice.core.audit.exception.AuditTemplateProcessingException;
import com.ecaservice.core.audit.model.AuditContextParams;
import com.ecaservice.core.audit.service.template.AuditTemplateProcessorService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
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
    private static final String RETURN_VALUE_PARAM = "returnValue";

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
    public String process(String auditCode, EventType eventType, AuditContextParams auditContextParams) {
        log.debug("Starting to process template for audit code [{}], event type [{}], context: {}", auditCode,
                eventType, auditContextParams);
        String templateCode = String.format(TEMPLATE_CODE_FORMAT, auditCode, eventType);
        try {
            Template template = configuration.getTemplate(templateCode);
            Map<String, Object> context = newHashMap();
            if (CollectionUtils.isEmpty(auditContextParams.getInputParams())) {
                context.putAll(auditContextParams.getInputParams());
            }
            if (auditContextParams.getReturnValue() != null) {
                context.put(RETURN_VALUE_PARAM, auditContextParams.getReturnValue());
            }
            String message = processTemplateIntoString(template, context);
            log.debug("Message [{}] for template [{}] has been processed", message, templateCode);
            return message;
        } catch (IOException | TemplateException ex) {
            log.error("There was an error while template [{}] processing: {}", templateCode, ex.getMessage());
            throw new AuditTemplateProcessingException(ex.getMessage());
        }
    }
}
