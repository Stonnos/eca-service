package com.ecaservice.audit.controller.audit;

import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.audit.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.ecaservice.audit.controller.doc.ApiExamples.AUDIT_EVENT_REQUEST_JSON;

/**
 * Implements REST API for audit events.
 *
 * @author Roman Batygin
 */
@Tag(name = "Audit events API for services")
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
    @Operation(
            description = "Saves audit event into database",
            summary = "Saves audit event into database",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(value = AUDIT_EVENT_REQUEST_JSON)
                    })
            })
    )
    @PostMapping(value = "/save")
    public void saveAuditEvent(@Valid @RequestBody AuditEventRequest auditEventRequest) {
        log.debug("Request audit event: {}", auditEventRequest);
        auditLogService.save(auditEventRequest);
    }
}
