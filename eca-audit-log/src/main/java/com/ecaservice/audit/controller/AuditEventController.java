package com.ecaservice.audit.controller;

import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.audit.service.AuditLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Implements REST API for audit events.
 *
 * @author Roman Batygin
 */
@Api(tags = "Audit events API for services")
@Slf4j
@RestController
@RequestMapping("/api/audit/event")
@RequiredArgsConstructor
public class AuditEventController {

    private final AuditLogService auditLogService;

    /**
     * Saves audit event into database.
     *
     * @param auditEventRequest - audit event request
     */
    @ApiOperation(
            value = "Saves audit event into database",
            notes = "Saves audit event into database"
    )
    @PostMapping(value = "/save")
    public void saveAuditEvent(@Valid @RequestBody AuditEventRequest auditEventRequest) {
        log.debug("Request audit event: {}", auditEventRequest);
        auditLogService.save(auditEventRequest);
    }
}
