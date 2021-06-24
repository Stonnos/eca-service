package com.ecaservice.core.audit.service.template;

import com.ecaservice.audit.dto.EventType;
import com.ecaservice.core.audit.model.AuditContextParams;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
     * @param auditCode          - audit code
     * @param eventType          - event type
     * @param auditContextParams - audit context params
     * @return result message
     */
    @NotBlank
    String process(@NotBlank String auditCode, @NotNull EventType eventType,
                   @NotNull AuditContextParams auditContextParams);
}
