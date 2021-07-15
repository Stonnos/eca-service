package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create instances result dto model.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Create instances result model")
public class CreateInstancesResultDto {

    /**
     * Instances id
     */
    @Schema(description = "Instances id", required = true)
    private Long id;

    /**
     * Source file name
     */
    @Schema(description = "Source file name", required = true)
    private String sourceFileName;

    /**
     * Database table name
     */
    @Schema(description = "Database table name", required = true)
    private String tableName;

    /**
     * Is instances created?
     */
    @Schema(description = "Instances creation boolean flag", required = true)
    private boolean created;

    /**
     * Error message
     */
    @Schema(description = "Error message")
    private String errorMessage;
}
