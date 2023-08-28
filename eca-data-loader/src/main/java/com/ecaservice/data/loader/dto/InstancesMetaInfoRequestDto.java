package com.ecaservice.data.loader.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.ecaservice.data.loader.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.data.loader.util.FieldConstraints.VALUE_1;
import static com.ecaservice.data.loader.util.FieldConstraints.VALUE_2;

/**
 * Instances meta info request dto.
 *
 * @author Roman Batygin
 */
@Data
@Builder
@Schema(description = "Instances meta info request dto")
public class InstancesMetaInfoRequestDto {

    /**
     * Instances name
     */
    @NotBlank
    @Size(min = VALUE_1, max = MAX_LENGTH_255)
    @Schema(description = "Instances name", example = "iris", requiredMode = Schema.RequiredMode.REQUIRED)
    private String relationName;

    /**
     * Instances size
     */
    @NotNull
    @Min(VALUE_2)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Instances number", example = "150", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer numInstances;

    /**
     * Attributes number
     */
    @NotNull
    @Min(VALUE_2)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Attributes number", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer numAttributes;

    /**
     * Class name
     */
    @NotBlank
    @Size(min = VALUE_1, max = MAX_LENGTH_255)
    @Schema(description = "Class name", example = "class", requiredMode = Schema.RequiredMode.REQUIRED)
    private String className;
}
