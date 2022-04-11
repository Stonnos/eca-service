package com.ecaservice.auto.test.dto;

import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.model.TestResult;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

import static com.ecaservice.auto.test.dto.FieldConstraints.DATE_TIME_PATTERN;

/**
 * Base test dto.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Base test dto")
public abstract class BaseTestDto {

    /**
     * Created date
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "Created date")
    private LocalDateTime created;

    /**
     * Started date
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "Started date")
    private LocalDateTime started;

    /**
     * Finished date
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "Finished date")
    private LocalDateTime finished;

    /**
     * Total time
     */
    @Schema(description = "Total time", pattern = "HH:mm:ss:SSSS", example = "00:00:35:5467")
    private String totalTime;

    /**
     * Test execution status
     */
    @Schema(description = "Test execution status")
    private ExecutionStatus executionStatus;

    /**
     * Details string
     */
    @Schema(description = "Details string")
    private String details;

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
}
