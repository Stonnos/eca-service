package com.ecaservice.notification.dto;

import com.ecaservice.notification.entity.NotificationEventType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
    private NotificationEventType eventType;

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
