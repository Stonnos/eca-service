package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

import static com.ecaservice.web.dto.util.FieldConstraints.DATE_TIME_PATTERN;

/**
 * Audit log model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Audit log model")
public class AuditLogDto {

    /**
     * Audit event id.
     */
    @ApiModelProperty(value = "Audit event id")
    private String eventId;

    /**
     * Audit message
     */
    @ApiModelProperty(value = "Audit message")
    private String message;

    /**
     * Event initiator
     */
    @ApiModelProperty(value = "Event initiator")
    private String initiator;

    /**
     * Audit group
     */
    @ApiModelProperty(value = "Audit group")
    private String groupCode;

    /**
     * Audit group title
     */
    @ApiModelProperty(value = "Audit group title")
    private String groupTitle;

    /**
     * Audit code
     */
    @ApiModelProperty(value = "Audit code")
    private String code;

    /**
     * Audit code title
     */
    @ApiModelProperty(value = "Audit code title")
    private String codeTitle;

    /**
     * Event date
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @ApiModelProperty(value = "Event date")
    private LocalDateTime eventDate;
}
