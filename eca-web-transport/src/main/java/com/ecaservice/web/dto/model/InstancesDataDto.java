package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Instances data dto.
 *
 * @author Roman Batygin
 */
@Data
@Builder
@Schema(description = "Instances data model")
public class InstancesDataDto {

    /**
     * Attributes list
     */
    @Schema(description = "Attributes list")
    private List<String> attributes;

    /**
     * Data rows
     */
    @Schema(description = "Data rows")
    private List<List<String>> rows;
}
