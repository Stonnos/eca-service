package com.ecaservice.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;

import static com.ecaservice.oauth.util.FieldConstraints.NOTIFICATION_EVENT_OPTIONS_MAX_SIZE;

/**
 * Update user notification options model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Update user notification options model")
public class UpdateUserNotificationOptionsDto {

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

    /**
     * Notification event options list
     */
    @Valid
    @Size(max = NOTIFICATION_EVENT_OPTIONS_MAX_SIZE)
    @Schema(description = "Notification event options list")
    private List<UpdateUserNotificationEventOptionsDto> notificationEventOptions;
}
