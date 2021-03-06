package com.ecaservice.audit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static com.ecaservice.audit.dto.FieldConstraints.DATE_TIME_PATTERN;
import static com.ecaservice.audit.dto.FieldConstraints.MAX_LENGTH_255;

/**
 * Audit event request model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Audit event request mode")
public class AuditEventRequest {

    /**
     * Audit event id.
     */
    @NotEmpty
    @Size(max = MAX_LENGTH_255)
    @ApiModelProperty(value = "Audit event id", example = "a01ebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
    private String eventId;

    /**
     * Audit message
     */
    @NotEmpty
    @ApiModelProperty(value = "Audit message", example = "Audit message")
    private String message;

    /**
     * Event initiator
     */
    @NotEmpty
    @Size(max = MAX_LENGTH_255)
    @ApiModelProperty(value = "Event initiator", example = "user")
    private String initiator;

    /**
     * Event type
     */
    @NotNull
    @ApiModelProperty(value = "Event type")
    private EventType eventType;

    /**
     * Audit group
     */
    @NotEmpty
    @Size(max = MAX_LENGTH_255)
    @ApiModelProperty(value = "Audit group", example = "GROUP_CODE")
    private String groupCode;

    /**
     * Audit group title
     */
    @Size(max = MAX_LENGTH_255)
    @ApiModelProperty(value = "Audit group title")
    private String groupTitle;

    /**
     * Audit code
     */
    @NotEmpty
    @Size(max = MAX_LENGTH_255)
    @ApiModelProperty(value = "Audit code", example = "AUDIT_CODE")
    private String code;

    /**
     * Audit code title
     */
    @Size(max = MAX_LENGTH_255)
    @ApiModelProperty(value = "Audit code title")
    private String codeTitle;

    /**
     * Event date
     */
    @NotNull
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @ApiModelProperty(value = "Event date", example = "2021-06-16 15:00:00")
    private LocalDateTime eventDate;
}
