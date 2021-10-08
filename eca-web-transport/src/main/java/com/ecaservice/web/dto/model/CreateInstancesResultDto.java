package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * Create instances result dto model.
 *
 * @author Roman Batygin
 */
@Data
@Builder
@Schema(description = "Create instances result model")
public class CreateInstancesResultDto {

    /**
     * Instances id
     */
    @Schema(description = "Instances id", required = true, example = "1")
    private Long id;

    /**
     * Source file name
     */
    @Schema(description = "Source file name", required = true, example = "iris.xls")
    private String sourceFileName;

    /**
     * Database table name
     */
    @Schema(description = "Database table name", required = true, example = "iris")
    private String tableName;
}
