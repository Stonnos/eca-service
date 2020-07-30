package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "Create instances result model")
public class CreateInstancesResultDto {

    /**
     * Instances id
     */
    @ApiModelProperty(value = "Instances id", required = true)
    private Long id;

    /**
     * Source file name
     */
    @ApiModelProperty(value = "Source file name", required = true)
    private String sourceFileName;

    /**
     * Database table name
     */
    @ApiModelProperty(value = "Database table name", required = true)
    private String tableName;

    /**
     * Is instances created?
     */
    @ApiModelProperty(value = "Instances creation boolean flag", required = true)
    private boolean created;

    /**
     * Error message
     */
    @ApiModelProperty(value = "Error message")
    private String errorMessage;
}
