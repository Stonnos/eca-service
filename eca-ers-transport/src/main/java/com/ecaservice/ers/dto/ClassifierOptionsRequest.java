package com.ecaservice.ers.dto;


import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

import static com.ecaservice.ers.dto.Constraints.MAX_LENGTH_255;
import static com.ecaservice.ers.dto.Constraints.SORT_FIELDS_MAX_ITEMS;

/**
 * Classifier options request model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Classifier options request model")
public class ClassifierOptionsRequest {

    /**
     * Instances name
     */
    @NotBlank
    @Size(max = MAX_LENGTH_255)
    @Schema(description = "Instances name", example = "iris", required = true)
    private String relationName;

    /**
     * Instances MD5 hash sum
     */
    @NotBlank
    @Size(max = MAX_LENGTH_255)
    @Schema(description = "Instances MD5 hash sum", example = "3032e188204cb537f69fc7364f638641", required = true)
    private String dataHash;

    /**
     * Evaluation method report
     */
    @Valid
    @NotNull
    @Schema(description = "Evaluation method report", required = true)
    private EvaluationMethodReport evaluationMethodReport;

    /**
     * Sort fields list
     */
    @Valid
    @Size(max = SORT_FIELDS_MAX_ITEMS)
    @ArraySchema(schema = @Schema(description = "Sort fields list"))
    private List<@NotNull SortField> sortFields;
}
