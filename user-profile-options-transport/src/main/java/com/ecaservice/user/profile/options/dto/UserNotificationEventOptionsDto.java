package com.ecaservice.user.profile.options.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * User notification event options dto.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "User notification event options")
public class UserNotificationEventOptionsDto {

    /**
     * Notification event type
     */
    @Schema(description = "Notification event type", example = "EXPERIMENT_STATUS_CHANGE")
    private UserNotificationEventType eventType;

    /**
     * Email notifications enabled?
     */
    @Schema(description = "Email notifications enabled?")
    private boolean emailEnabled;

    /**
     * Web push notifications enabled?
     */
    @Schema(description = "Web push notifications enabled?")
    private boolean webPushEnabled;

    /**
     * Email notifications supported?
     */
    @Schema(description = "Email notifications supported?")
    private boolean emailSupported;

    /**
     * Web push notifications supported?
     */
    @Schema(description = "Web push notifications supported?")
    private boolean webPushSupported;
}
