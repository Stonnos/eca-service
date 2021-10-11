package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Evaluation logs page dto.
 *
 * @author Roman Batygin
 */
@Schema(description = "Evaluation logs page dto")
public class EvaluationLogsPageDto extends PageDto<EvaluationLogDto> {
}
