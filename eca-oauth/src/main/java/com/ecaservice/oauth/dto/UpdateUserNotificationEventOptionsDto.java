package com.ecaservice.oauth.dto;

import com.ecaservice.user.profile.options.dto.UserNotificationEventType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;

/**
 * Update user notification options model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Update user notification options model")
public class UpdateUserNotificationEventOptionsDto {

    /**
     * Notification event type
     */
    @NotNull
    @Schema(description = "Notification event type", requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = MAX_LENGTH_255, example = "EXPERIMENT_STATUS_CHANGE")
    private UserNotificationEventType eventType;

    /**
     * Email notifications enabled? (global flag)
     */
    @Schema(description = "Email notifications enabled? (global flag)")
    private boolean emailEnabled;

    /**
     * Web push notifications enabled? (global flag)
     */
    @Schema(description = "Web push notifications enabled? (global flag)")
    private boolean webPushEnabled;
}
