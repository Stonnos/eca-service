package com.ecaservice.auto.test.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import eca.core.evaluation.EvaluationMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Base evaluation request dto.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EvaluationRequestDto.class),
        @JsonSubTypes.Type(value = ExperimentRequestDto.class),
        @JsonSubTypes.Type(value = EvaluationRequestExtendedDto.class),
        @JsonSubTypes.Type(value = ExperimentRequestExtendedDto.class)
})
@Schema(description = "Base evaluation request dto")
public abstract class BaseEvaluationRequestDto extends BaseTestDto {

    /**
     * Request id from eca - server
     */
    @Schema(description = "Request id from eca - server")
    private String requestId;

    /**
     * Evaluation method
     */
    @Schema(description = "Evaluation method")
    private EvaluationMethod evaluationMethod;

    /**
     * Evaluation method description
     */
    @Schema(description = "Evaluation method description")
    private String evaluationMethodDescription;

    /**
     * Instances name
     */
    @Schema(description = "Relation name")
    private String relationName;

    /**
     * Instances number
     */
    @Schema(description = "Instances number")
    private Integer numInstances;

    /**
     * Attributes number
     */
    @Schema(description = "Attributes number")
    private Integer numAttributes;
}
