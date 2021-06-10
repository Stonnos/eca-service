package com.ecaservice.core.audit.service.template;

import com.ecaservice.core.audit.entity.EventType;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Template processor interface.
 *
 * @author Roman Batygin
 */
@Validated
public interface AuditTemplateProcessorService {

    /**
     * Creates template based on input parameters.
     *
     * @param auditCode - audit code
     * @param eventType - event type
     * @param variables - template variables map
     * @return result message
     */
    @NotBlank
    String process(@NotBlank String auditCode, @NotNull EventType eventType, Map<String, String> variables);
}
