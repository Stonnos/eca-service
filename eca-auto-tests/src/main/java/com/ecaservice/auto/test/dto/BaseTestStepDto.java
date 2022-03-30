package com.ecaservice.auto.test.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
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
@JsonSubTypes({
        @JsonSubTypes.Type(value = EmailTestStepDto.class),
        @JsonSubTypes.Type(value = ExperimentResultsTestStepDto.class),
        @JsonSubTypes.Type(value = EvaluationResultsTestStepDto.class)
})
public abstract class BaseTestStepDto extends BaseTestDto {
}
