package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LONG_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.UUID_MAX_LENGTH;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1_STRING;

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
    @Schema(description = "Instances id", required = true, example = "1", minimum = VALUE_1_STRING,
            maximum = MAX_LONG_VALUE_STRING)
    private Long id;

    /**
     * Instances uuid
     */
    @Schema(description = "Instances uuid", required = true, example = "1d2de514-3a87-4620-9b97-c260e24340de",
            maxLength = UUID_MAX_LENGTH)
    private String uuid;

    /**
     * Source file name
     */
    @Schema(description = "Source file name", required = true, example = "iris.xls", maxLength = MAX_LENGTH_255)
    private String sourceFileName;

    /**
     * Database table name
     */
    @Schema(description = "Database table name", required = true, example = "iris", maxLength = MAX_LENGTH_255)
    private String tableName;
}
