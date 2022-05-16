package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;

/**
 * Classifier info dto.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Classifier info model")
public class ClassifierInfoDto {

    /**
     * Classifier name
     */
    @Schema(description = "Classifier name", example = "CART", maxLength = MAX_LENGTH_255)
    private String classifierName;

    /**
     * Classifier description
     */
    @Schema(description = "Classifier description", example = "CART", maxLength = MAX_LENGTH_255)
    private String classifierDescription;

    /**
     * Is meta classifier (used for stacking algorithms)
     */
    @Schema(description = "Is meta classifier (used for stacking algorithms)")
    private boolean metaClassifier;

    /**
     * Classifier input options map
     */
    @ArraySchema(schema = @Schema(description = "Classifier input options list"))
    private List<InputOptionDto> inputOptions;

    /**
     * Individual classifiers
     */
    @ArraySchema(schema = @Schema(description = "Individual classifiers"))
    private List<ClassifierInfoDto> individualClassifiers;
}
