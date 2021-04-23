package com.ecaservice.ers.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

import static com.ecaservice.ers.dto.Constraints.MIN_1;
import static com.ecaservice.ers.dto.Constraints.MIN_2;

/**
 * Evaluation method report model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Evaluation method report model")
public class EvaluationMethodReport {

    /**
     * Evaluation method
     */
    @NotNull
    @ApiModelProperty(value = "Evaluation method")
    private EvaluationMethod evaluationMethod;

    /**
     * Folds number for k * V cross - validation method
     */
    @Min(MIN_2)
    @ApiModelProperty(value = "Folds number for k * V cross - validation method", example = "2")
    private BigInteger numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    @Min(MIN_1)
    @ApiModelProperty(value = "Tests number for k * V cross - validation method", example = "1")
    private BigInteger numTests;

    /**
     * Seed value for k * V cross - validation method
     */
    @ApiModelProperty(value = "Seed value for k * V cross - validation method")
    private BigInteger seed;
}
