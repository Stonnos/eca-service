package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

import static com.ecaservice.web.dto.util.FieldConstraints.DATE_TIME_PATTERN;

/**
 * Experiment dto model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Experiment model")
public class ExperimentDto extends AbstractEvaluationDto {

    /**
     * First name
     */
    @Schema(description = "Request creator first name", example = "Roman")
    private String firstName;

    /**
     * Email
     */
    @Schema(description = "Request creator email", example = "test@mail.ru")
    private String email;

    /**
     * Experiment file absolute path
     */
    @Schema(description = "Experiment results file", example = "experiment_1d2de514-3a87-4620-9b97-c260e24340de.model")
    private String experimentAbsolutePath;

    /**
     * Training data absolute path
     */
    @Schema(description = "Training data file", example = "data_1d2de514-3a87-4620-9b97-c260e24340de.xls")
    private String trainingDataAbsolutePath;

    /**
     * Date when experiment results is sent
     */
    @Schema(description = "Experiment results sent date", type = "string", example = "2021-07-01 14:00:00")
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime sentDate;

    /**
     * Experiment files deleted date
     */
    @Schema(description = "Experiment files delete date", type = "string", example = "2021-07-01 14:00:00")
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime deletedDate;

    /**
     * Experiment type
     */
    @Schema(description = "Experiment type")
    private EnumDto experimentType;
}
