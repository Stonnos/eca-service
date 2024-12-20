package com.ecaservice.ers.dto;


import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

import static com.ecaservice.ers.dto.Constraints.MIN_1;
import static com.ecaservice.ers.dto.Constraints.SORT_FIELDS_MAX_ITEMS;
import static com.ecaservice.ers.dto.Constraints.UUID_MAX_SIZE;
import static com.ecaservice.ers.dto.Constraints.UUID_PATTERN;

/**
 * Classifier options request model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Classifier options request model")
public class ClassifierOptionsRequest {

    /**
     * Request id
     */
    @NotBlank
    @Pattern(regexp = UUID_PATTERN)
    @Size(min = MIN_1, max = UUID_MAX_SIZE)
    @Schema(description = "Request id", example = "1d2de514-3a87-4620-9b97-c260e24340de",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String requestId;

    /**
     * Instances uuid
     */
    @NotBlank
    @Size(min = MIN_1, max = UUID_MAX_SIZE)
    @Schema(description = "Instances uuid", example = "f8cecbf7-405b-403b-9a94-f51e8fb73ed8",
            maxLength = UUID_MAX_SIZE, requiredMode = Schema.RequiredMode.REQUIRED)
    private String dataUuid;

    /**
     * Evaluation method report
     */
    @Valid
    @NotNull
    @Schema(description = "Evaluation method report", requiredMode = Schema.RequiredMode.REQUIRED)
    private EvaluationMethodReport evaluationMethodReport;

    /**
     * Evaluation results statistics sort fields list
     */
    @Valid
    @Size(max = SORT_FIELDS_MAX_ITEMS)
    @ArraySchema(schema = @Schema(description = "Evaluation results statistics sort fields list"))
    private List<@NotNull EvaluationResultsStatisticsSortField> evaluationResultsStatisticsSortFields;
}
