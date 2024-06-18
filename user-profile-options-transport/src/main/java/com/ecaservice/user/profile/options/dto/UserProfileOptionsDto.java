package com.ecaservice.user.profile.options.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

import static com.ecaservice.user.profile.options.dto.Constraints.MAX_LENGTH_255;
import static com.ecaservice.user.profile.options.dto.Constraints.MIN_1;
import static com.ecaservice.user.profile.options.dto.Constraints.MIN_ZERO;

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
    @NotBlank
    @Size(min = MIN_1, max = MAX_LENGTH_255)
    @Schema(description = "User login")
    private String user;

    /**
     * User profile options version
     */
    @NotNull
    @Min(MIN_ZERO)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "User profile options version")
    private Integer version;

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
    @Schema(description = "Notification event options list")
    private List<UserNotificationEventOptionsDto> notificationEventOptions;
}
