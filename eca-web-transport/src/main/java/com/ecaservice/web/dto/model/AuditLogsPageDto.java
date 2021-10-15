package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Audit logs page dto.
 *
 * @author Roman Batygin
 */
@Schema(description = "Audit logs page dto")
public class AuditLogsPageDto extends PageDto<AuditLogDto> {
}
