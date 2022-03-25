package com.ecaservice.auto.test.dto;

import com.ecaservice.auto.test.entity.autotest.AutoTestType;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
     * Evaluation requests list
     */
    @ArraySchema(schema = @Schema(description = "Evaluation requests"))
    private List<BaseEvaluationRequestDto> requests;

    /**
     * Details string
     */
    @Schema(description = "Details string")
    private String details;
}
