package com.ecaservice.server.dto;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static com.ecaservice.server.util.FieldConstraints.MAX_FOLDS;
import static com.ecaservice.server.util.FieldConstraints.MAX_TESTS;
import static com.ecaservice.server.util.FieldConstraints.MIN_FOLDS;
import static com.ecaservice.server.util.FieldConstraints.MIN_TESTS;

/**
 * Experiment request dto.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Evaluation request")
public class CreateEvaluationRequestDto extends AbstractEvaluationRequestDto {

    /**
     * Classifier input options json config
     */
    @Valid
    @NotNull
    @Schema(description = "Classifier options json", requiredMode = Schema.RequiredMode.REQUIRED)
    private ClassifierOptions classifierOptions;

    /**
     * Folds number for k * V cross - validation method
     */
    @Min(MIN_FOLDS)
    @Max(MAX_FOLDS)
    @Schema(description = "Folds number for k * V cross - validation method", example = "10")
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    @Min(MIN_TESTS)
    @Max(MAX_TESTS)
    @Schema(description = "Tests number for k * V cross - validation method", example = "1")
    private Integer numTests;

    /**
     * Seed value for k * V cross - validation method
     */
    @Min(Integer.MIN_VALUE)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Seed value for k * V cross - validation method", example = "1")
    private Integer seed;
}
