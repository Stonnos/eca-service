package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LONG_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1_STRING;

/**
 * Classify instance request dto.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Classify instance request model")
public class ClassifyInstanceRequestDto {

    /**
     * Model id
     */
    @NotNull
    @Schema(description = "Model id", example = "1", minimum = VALUE_1_STRING, maximum = MAX_LONG_VALUE_STRING)
    private Long modelId;

    /**
     * Instance values list
     */
    @Valid
    @NotEmpty
    @ArraySchema(schema = @Schema(description = "Instance values list"))
    private List<ClassifyInstanceValueDto> values;
}
