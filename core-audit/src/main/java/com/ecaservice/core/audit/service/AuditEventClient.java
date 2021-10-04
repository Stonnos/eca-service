package com.ecaservice.core.audit.service;

import com.ecaservice.audit.dto.AuditEventRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Interface for sending audit events to specified channel.
 *
 * @author Roman Batygin
 */
@FeignClient(value = "eca-audit-log", path = "/api/audit/event")
public interface AuditEventClient {

    /**
     * Sends audit event request.
     *
     * @param auditEventRequest audit event request
     */
    @PostMapping(value = "/save")
    void sendEvent(@RequestBody AuditEventRequest auditEventRequest);
}
