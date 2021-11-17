package com.ecaservice.external.api.dto;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Validation error response payload dto.
 *
 * @author Roman Batygin
 */
@Schema(description = "Validation error response payload model")
public class ValidationErrorResponsePayloadDto extends ResponseDto<List<ValidationErrorDto>> {
}
