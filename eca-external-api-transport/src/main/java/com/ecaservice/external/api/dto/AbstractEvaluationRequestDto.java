package com.ecaservice.external.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

import static com.ecaservice.external.api.dto.Constraints.MAX_LENGTH_255;
import static com.ecaservice.external.api.dto.Constraints.MIN_LENGTH_1;

/**
 * Abstract evaluation request dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Base evaluation request model")
public class AbstractEvaluationRequestDto implements Serializable {

    /**
     * Training data uuid
     */
    @NotBlank
    @Size(min = MIN_LENGTH_1, max = MAX_LENGTH_255)
    @Schema(description = "Training data uuid", example = "1d2de514-3a87-4620-9b97-c260e24340de",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String trainDataUuid;
}
