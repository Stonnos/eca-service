package com.ecaservice.ers.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

import static com.ecaservice.ers.dto.Constraints.MAX_LENGTH_255;

/**
 * Classifier report model.
 *
 * @author Roman Batygin
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EnsembleClassifierReport.class, name = "ensemble_classifier_report"),
})
@Data
@Schema(description = "Classifier report model")
public class ClassifierReport {

    /**
     * Classifier name
     */
    @NotBlank
    @Size(max = MAX_LENGTH_255)
    @Schema(description = "Classifier name", example = "CART", required = true)
    private String classifierName;

    /**
     * Classifier description
     */
    @Size(max = MAX_LENGTH_255)
    @Schema(description = "Classifier description")
    private String classifierDescription;

    /**
     * Classifier options string
     */
    @NotBlank
    @Schema(description = "Classifier options string", example = "classifier options string", required = true)
    private String options;

    /**
     * Classifier input options
     */
    @Valid
    @Schema(description = "Classifier input options")
    private List<ClassifierInputOption> classifierInputOptions;

    /**
     * Is meta classifier?
     */
    @Schema(description = "Is meta classifier?")
    private boolean metaClassifier;
}
