package com.ecaservice.web.push.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

import static com.ecaservice.web.push.dto.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.push.dto.FieldConstraints.RECEIVERS_MAX_SIZE;
import static com.ecaservice.web.push.dto.FieldConstraints.VALUE_1;

/**
 * User notification push request.
 *
 * @author Roman Batygin
 */
@Getter
@Setter
@Schema(description = "User notification push request")
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
    @NotBlank
    @Size(min = VALUE_1, max = MAX_LENGTH_255)
    @Schema(description = "Initiator user")
    private String initiator;

    /**
     * Receivers list
     */
    @NotEmpty
    @Size(min = VALUE_1, max = RECEIVERS_MAX_SIZE)
    @Schema(description = "Receivers list")
    private List<String> receivers;
}
