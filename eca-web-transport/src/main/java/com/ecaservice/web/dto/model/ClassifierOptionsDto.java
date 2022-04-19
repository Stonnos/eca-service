package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.DATE_TIME_PATTERN;
import static com.ecaservice.web.dto.util.FieldConstraints.LOCAL_DATE_TIME_MAX_LENGTH;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LONG_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1_STRING;

/**
 * Classifier input options dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Classifier json input options model")
public class ClassifierOptionsDto {

    @Schema(description = "Options id", example = "1", minimum = VALUE_1_STRING, maximum = MAX_LONG_VALUE_STRING)
    private Long id;

    /**
     * Options name
     */
    @Schema(description = "Options name", required = true, example = "DecisionTreeOptions", maxLength = MAX_LENGTH_255)
    private String optionsName;

    /**
     * Creation date
     */
    @Schema(description = "Creation date", required = true, type = "string", example = "2021-07-01 14:00:00",
            maxLength = LOCAL_DATE_TIME_MAX_LENGTH)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationDate;

    /**
     * User name
     */
    @Schema(description = "User name", example = "admin", maxLength = MAX_LENGTH_255)
    private String createdBy;

    /**
     * Json config
     */
    @Schema(description = "Json config", required = true, example = "Json config")
    private String config;

    /**
     * Classifier input options list
     */
    @ArraySchema(schema = @Schema(description = "Classifier input options list"))
    private List<InputOptionDto> inputOptions;
}
