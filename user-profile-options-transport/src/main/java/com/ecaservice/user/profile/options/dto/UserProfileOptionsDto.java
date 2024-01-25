package com.ecaservice.user.profile.options.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * User profile options dto.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "User profile options")
public class UserProfileOptionsDto {

    /**
     * User login
     */
    @Schema(description = "User login")
    private String user;

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
    private List<UserNotificationEventOptionsDto> notificationEventOptions;
}
