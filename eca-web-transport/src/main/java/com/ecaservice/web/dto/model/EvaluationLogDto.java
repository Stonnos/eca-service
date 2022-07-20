package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Evaluation log dto model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Classifier evaluation log model")
public class EvaluationLogDto extends AbstractEvaluationDto {

    /**
     * Classifier info
     */
    @Schema(description = "Classifier info")
    private ClassifierInfoDto classifierInfo;
}
