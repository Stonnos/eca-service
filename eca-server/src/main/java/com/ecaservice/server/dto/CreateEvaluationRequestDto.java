package com.ecaservice.server.dto;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Evaluation request dto.
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
}
