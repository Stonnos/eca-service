package com.ecaservice.web.push.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

import static com.ecaservice.web.push.dto.FieldConstraints.DATE_TIME_PATTERN;
import static com.ecaservice.web.push.dto.FieldConstraints.LOCAL_DATE_TIME_MAX_LENGTH;
import static com.ecaservice.web.push.dto.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.push.dto.FieldConstraints.RECEIVERS_MAX_SIZE;
import static com.ecaservice.web.push.dto.FieldConstraints.VALUE_1;

/**
 * User push notification request.
 *
 * @author Roman Batygin
 */
@Getter
@Setter
@Schema(description = "User push notification request")
public class UserPushNotificationRequest extends AbstractPushRequest {

    /**
     * Creates user push notification request
     */
    public UserPushNotificationRequest() {
        super(PushType.USER_NOTIFICATION);
    }

    /**
     * Initiator user
     */
    @Size(max = MAX_LENGTH_255)
    @Schema(description = "Initiator user")
    private String initiator;

    /**
     * Receivers list
     */
    @NotEmpty
    @Size(min = VALUE_1, max = RECEIVERS_MAX_SIZE)
    @Schema(description = "Receivers list")
    private List<String> receivers;

    /**
     * Created date
     */
    @NotNull
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Schema(description = "Created date", pattern = DATE_TIME_PATTERN, maxLength = LOCAL_DATE_TIME_MAX_LENGTH,
            example = "2022-09-22 14:00:00")
    private LocalDateTime created;
}
