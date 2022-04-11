package com.ecaservice.external.api.test.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * Auto tests job model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Auto tests job model")
public class AutoTestsJobDto extends BaseTestDto {

    /**
     * Auto tests job uuid
     */
    @Schema(description = "Auto tests job uuid")
    private String jobUuid;

    /**
     * Success tests
     */
    @Schema(description = "Success tests")
    private Integer success;

    /**
     * Failed tests
     */
    @Schema(description = "Failed tests")
    private Integer failed;

    /**
     * Errors tests
     */
    @Schema(description = "Errors tests")
    private Integer errors;

    /**
     * Test results list
     */
    @ArraySchema(schema = @Schema(description = "Test results"))
    private List<AutoTestDto> testResults;

}
