package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Email template page dto.
 *
 * @author Roman Batygin
 */
@Schema(description = "Email template page dto")
public class EmailTemplatesPageDto extends PageDto<EmailTemplateDto> {
}
