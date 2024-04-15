package com.ecaservice.ers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigInteger;

import static com.ecaservice.ers.dto.Constraints.MAX_LENGTH_255;
import static com.ecaservice.ers.dto.Constraints.MIN_1;
import static com.ecaservice.ers.dto.Constraints.MIN_2;

/**
 * Training data report model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Training data report model")
public class InstancesReport {

    /**
     * Instances MD5 hash sum
     */
    @NotBlank
    @Size(min = MIN_1, max = MAX_LENGTH_255)
    @Schema(description = "Instances MD5 hash sum", example = "3032e188204cb537f69fc7364f638641",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String dataMd5Hash;

    /**
     * Relation name
     */
    @NotBlank
    @Size(min = MIN_1, max = MAX_LENGTH_255)
    @Schema(description = "Relation name", example = "iris", requiredMode = Schema.RequiredMode.REQUIRED)
    private String relationName;

    /**
     * Instances number
     */
    @NotNull
    @Min(MIN_2)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Instances number", example = "150", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigInteger numInstances;

    /**
     * Attributes number
     */
    @NotNull
    @Min(MIN_2)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Attributes number", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigInteger numAttributes;

    /**
     * Classes number
     */
    @NotNull
    @Min(MIN_2)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Classes number", example = "4", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigInteger numClasses;

    /**
     * Class name
     */
    @NotBlank
    @Size(min = MIN_1, max = MAX_LENGTH_255)
    @Schema(description = "Class name", example = "class", requiredMode = Schema.RequiredMode.REQUIRED)
    private String className;
}
