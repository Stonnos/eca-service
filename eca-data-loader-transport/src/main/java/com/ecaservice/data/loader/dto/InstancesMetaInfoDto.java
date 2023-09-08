package com.ecaservice.data.loader.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import static com.ecaservice.data.loader.dto.FieldConstraints.DATE_TIME_PATTERN;
import static com.ecaservice.data.loader.dto.FieldConstraints.LOCAL_DATE_TIME_MAX_LENGTH;

/**
 * Instances meta info dto.
 *
 * @author Roman Batygin
 */
@Data
@Builder
@Schema(description = "Instances meta info dto")
public class InstancesMetaInfoDto {

    /**
     * Instances uuid
     */
    @Schema(description = "Instances uuid", example = "f8cecbf7-405b-403b-9a94-f51e8fb73ed8", maxLength = FieldConstraints.UUID_MAX_SIZE)
    private String uuid;

    /**
     * Instances name
     */
    @Schema(description = "Instances name", example = "iris", maxLength = FieldConstraints.MAX_LENGTH_255)
    private String relationName;

    /**
     * Instances size
     */
    @Schema(description = "Instances number", example = "150", minimum = FieldConstraints.ZERO_VALUE_STRING,
            maximum = FieldConstraints.MAX_INTEGER_VALUE_STRING)
    private Integer numInstances;

    /**
     * Attributes number
     */
    @Schema(description = "Attributes number", example = "5", minimum = FieldConstraints.ZERO_VALUE_STRING,
            maximum = FieldConstraints.MAX_INTEGER_VALUE_STRING)
    private Integer numAttributes;

    /**
     * Classes number
     */
    @Schema(description = "Classes number", example = "4", minimum = FieldConstraints.ZERO_VALUE_STRING,
            maximum = FieldConstraints.MAX_INTEGER_VALUE_STRING)
    private Integer numClasses;

    /**
     * Class name
     */
    @Schema(description = "Class name", example = "class", maxLength = FieldConstraints.MAX_LENGTH_255)
    private String className;

    /**
     * Instances file MD5 hash sum
     */
    @Schema(description = "Instances file MD5 hash sum", example = "3032e188204cb537f69fc7364f638641",
            maxLength = FieldConstraints.MAX_LENGTH_255)
    private String md5Hash;

    /**
     * Instances object path in storage
     */
    @Schema(description = "Instances object path in storage",
            example = "instances-f8cecbf7-405b-403b-9a94-f51e8fb73ed8.json", maxLength = FieldConstraints.MAX_LENGTH_255)
    private String objectPath;

    /**
     * Object expiration date
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Schema(description = "Object expiration date", type = "string", example = "2021-07-01 14:00:00",
            maxLength = LOCAL_DATE_TIME_MAX_LENGTH)
    private LocalDateTime expireAt;
}
