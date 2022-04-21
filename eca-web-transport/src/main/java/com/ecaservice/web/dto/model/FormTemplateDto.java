package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;

/**
 * Form template dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Form template model")
public class FormTemplateDto {

    /**
     * Template name
     */
    @Schema(description = "Template name", example = "templateCode", maxLength = MAX_LENGTH_255)
    private String templateName;

    /**
     * Template title
     */
    @Schema(description = "Template title", example = "Template title", maxLength = MAX_LENGTH_255)
    private String templateTitle;

    /**
     * Object class
     */
    @Schema(description = "Object class", example = "Object", maxLength = MAX_LENGTH_255)
    private String objectClass;

    /**
     * Object type
     */
    @Schema(description = "Object type", example = "object_type", maxLength = MAX_LENGTH_255)
    private String objectType;

    /**
     * Fields list
     */
    @ArraySchema(schema = @Schema(description = "Form fields list"))
    private List<FormFieldDto> fields;
}
