package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

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
    @Schema(description = "Classifier name")
    private String classifierName;

    /**
     * Classifier input options map
     */
    @Schema(description = "Classifier input options list")
    private List<InputOptionDto> inputOptions;
}
