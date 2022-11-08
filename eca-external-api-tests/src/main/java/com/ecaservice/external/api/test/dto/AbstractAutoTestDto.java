package com.ecaservice.external.api.test.dto;

import com.ecaservice.external.api.dto.ResponseCode;
import com.ecaservice.test.common.model.MatchResult;
import com.ecaservice.test.common.model.TestResult;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Base auto test dto model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EvaluationRequestAutoTestDto.class),
        @JsonSubTypes.Type(value = ExperimentRequestAutoTestDto.class)
})
@Schema(description = "Base auto test dto model")
public abstract class AbstractAutoTestDto extends BaseTestDto {

    /**
     * Request id
     */
    @Schema(description = "Request id")
    private String requestId;

    /**
     * Display name (Test description)
     */
    @Schema(description = "Display name (Test description)")
    private String displayName;

    /**
     * Test result
     */
    @Schema(description = "Test result")
    private TestResult testResult;

    /**
     * Total matched
     */
    @Schema(description = "Total matched")
    private int totalMatched;

    /**
     * Total not matched
     */
    @Schema(description = "Total not matched")
    private int totalNotMatched;

    /**
     * Total not found
     */
    @Schema(description = "Total not found")
    private int totalNotFound;

    /**
     * Expected response code
     */
    @Schema(description = "Expected response code")
    private ResponseCode expectedResponseCode;

    /**
     * Actual response code
     */
    @Schema(description = "Actual response code")
    private ResponseCode actualResponseCode;

    /**
     * Response code match result
     */
    @Schema(description = "Response code match result")
    private MatchResult responseCodeMatchResult;

    /**
     * Request json
     */
    @Schema(description = "Request json")
    private String request;

    /**
     * Response json
     */
    @Schema(description = "Response json")
    private String response;
}
