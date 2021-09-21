package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * Instances data dto.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Instances data model")
public class InstancesDataDto {

    /**
     * Columns list
     */
    @Schema(description = "Columns list")
    private List<String> columns;

    /**
     * Data rows
     */
    @Schema(description = "Data rows")
    private List<List<String>> rows;
}
