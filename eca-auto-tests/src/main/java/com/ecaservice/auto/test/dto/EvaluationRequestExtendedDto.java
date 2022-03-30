package com.ecaservice.auto.test.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * Evaluation request extended dto.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EvaluationRequestExtendedDto extends EvaluationRequestDto {

    /**
     * Test steps
     */
    @ArraySchema(schema = @Schema(description = "Test steps"))
    private List<BaseTestStepDto> testSteps;
}
