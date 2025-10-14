package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Personal access token page dto.
 *
 * @author Roman Batygin
 */
@Schema(description = "UPersonal access token page dto")
public class PersonalAccessTokensPageDto extends PageDto<PersonalAccessTokenDto> {
}
