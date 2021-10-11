package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Users page dto.
 *
 * @author Roman Batygin
 */
@Schema(description = "Users page dto")
public class UsersPageDto extends PageDto<UserDto> {
}
