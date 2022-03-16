package com.ecaservice.auto.test.dto;

import com.ecaservice.auto.test.entity.autotest.AutoTestType;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

import static com.ecaservice.auto.test.dto.FieldConstraints.DATE_TIME_PATTERN;

/**
 * Auto tests job model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Auto tests job model")
public class AutoTestsJobDto {

    /**
     * Auto tests job uuid
     */
    @Schema(description = "Auto tests job uuid")
    private String jobUuid;

    /**
     * Auto test type
     */
    @Schema(description = "Auto test type")
    private AutoTestType autoTestType;

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
     * Test execution status
     */
    @Schema(description = "Test execution status")
    private ExecutionStatus executionStatus;

    /**
     * Details string
     */
    @Schema(description = "Details string")
    private String details;
}
