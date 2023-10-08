package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;

/**
 * Form templates group dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Form templates group model")
public class FormTemplateGroupDto {

    /**
     * Template name
     */
    @Schema(description = "Template name", example = "groupName", maxLength = MAX_LENGTH_255)
    private String groupName;

    /**
     * Templates list
     */
    @ArraySchema(schema = @Schema(description = "Templates list"))
    private List<FormTemplateDto> templates;
}
