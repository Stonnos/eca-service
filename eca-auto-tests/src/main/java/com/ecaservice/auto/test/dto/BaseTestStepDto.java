package com.ecaservice.auto.test.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Base test step dto.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EmailTestStepDto.class, name = "EMAIL_STEP"),
        @JsonSubTypes.Type(value = ExperimentResultsTestStepDto.class, name = "EXPERIMENT_RESULTS_STEP"),
        @JsonSubTypes.Type(value = EvaluationResultsTestStepDto.class, name = "EVALUATION_RESULTS_STEP")
})
public abstract class BaseTestStepDto extends BaseTestDto {
}
