package com.ecaservice.audit.controller.audit;

import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.audit.service.AuditLogService;
import com.ecaservice.common.web.dto.ValidationErrorDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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
                            @ExampleObject(
                                    name = "AuditRequest",
                                    ref = "#/components/examples/AuditRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "AuditEventBadRequestResponse",
                                                    ref = "#/components/examples/AuditEventBadRequestResponse"
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/save")
    public void saveAuditEvent(@Valid @RequestBody AuditEventRequest auditEventRequest) {
        log.debug("Request audit event: {}", auditEventRequest);
        auditLogService.save(auditEventRequest);
    }
}
