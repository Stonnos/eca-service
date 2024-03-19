package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * User profile notification options dto.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "User profile notification options")
public class UserProfileNotificationOptionsDto {

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
    @Schema(description = "Notification event options list")
    private List<UserProfileNotificationEventOptionsDto> notificationEventOptions;
}
