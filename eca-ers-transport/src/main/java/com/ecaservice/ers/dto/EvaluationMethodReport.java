package com.ecaservice.ers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigInteger;

import static com.ecaservice.ers.dto.Constraints.MAX_LENGTH_255;
import static com.ecaservice.ers.dto.Constraints.MIN_1;
import static com.ecaservice.ers.dto.Constraints.MIN_2;

/**
 * Evaluation method report model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Evaluation method report model")
public class EvaluationMethodReport {

    /**
     * Evaluation method
     */
    @NotNull
    @Schema(description = "Evaluation method", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = MAX_LENGTH_255)
    private EvaluationMethod evaluationMethod;

    /**
     * Folds number for k * V cross - validation method
     */
    @Min(MIN_2)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Folds number for k * V cross - validation method", example = "2")
    private BigInteger numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    @Min(MIN_1)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Tests number for k * V cross - validation method", example = "1")
    private BigInteger numTests;

    /**
     * Seed value for k * V cross - validation method
     */
    @Min(Long.MIN_VALUE)
    @Max(Long.MAX_VALUE)
    @Schema(description = "Seed value for k * V cross - validation method")
    private BigInteger seed;
}
