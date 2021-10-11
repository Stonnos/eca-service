package com.ecaservice.external.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Evaluation response wrapper dto.
 *
 * @author Roman Batygin
 */
@Schema(description = "Evaluation response wrapper model")
public class EvaluationResponseWrapperDto extends ResponseDto<EvaluationResponseDto> {
}
