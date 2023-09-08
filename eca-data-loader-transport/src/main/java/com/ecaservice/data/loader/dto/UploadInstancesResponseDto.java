package com.ecaservice.data.loader.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.time.LocalDateTime;

import static com.ecaservice.data.loader.dto.FieldConstraints.DATE_TIME_PATTERN;
import static com.ecaservice.data.loader.dto.FieldConstraints.LOCAL_DATE_TIME_MAX_LENGTH;
import static com.ecaservice.data.loader.dto.FieldConstraints.UUID_MAX_SIZE;

/**
 * Upload instances response dto.
 *
 * @author Roman Batygin
 */
@Data
@Builder
@Schema(description = "Upload instances response dto")
public class UploadInstancesResponseDto {

    @Tolerate
    public UploadInstancesResponseDto() {
        //default constructor
    }

    /**
     * Instances uuid
     */
    @Schema(description = "Instances uuid", example = "f8cecbf7-405b-403b-9a94-f51e8fb73ed8", maxLength = UUID_MAX_SIZE)
    private String uuid;

    /**
     * Instances file MD5 hash sum
     */
    @Schema(description = "Instances file MD5 hash sum", example = "3032e188204cb537f69fc7364f638641",
            maxLength = FieldConstraints.MAX_LENGTH_255)
    private String md5Hash;

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
