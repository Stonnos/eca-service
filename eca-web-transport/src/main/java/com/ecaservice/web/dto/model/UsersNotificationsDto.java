package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Users notifications page dto.
 *
 * @author Roman Batygin
 */
@Schema(description = "Users notifications page dto")
public class UsersNotificationsDto extends PageDto<UserNotificationDto> {
}
