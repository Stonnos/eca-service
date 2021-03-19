package com.ecaservice.oauth.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.ecaservice.oauth.util.FieldConstraints.PERSON_NAME_MAX_SIZE;
import static com.ecaservice.oauth.util.FieldConstraints.PERSON_NAME_REGEX;

/**
 * Update user info model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Update user info model")
public class UpdateUserInfoDto {

    /**
     * First name
     */
    @NotBlank
    @Size(max = PERSON_NAME_MAX_SIZE)
    @Pattern(regexp = PERSON_NAME_REGEX)
    @ApiModelProperty(value = "First name", required = true)
    private String firstName;

    /**
     * Last name
     */
    @NotBlank
    @Size(max = PERSON_NAME_MAX_SIZE)
    @Pattern(regexp = PERSON_NAME_REGEX)
    @ApiModelProperty(value = "Last name", required = true)
    private String lastName;

    /**
     * Middle name
     */
    @NotBlank
    @Size(max = PERSON_NAME_MAX_SIZE)
    @Pattern(regexp = PERSON_NAME_REGEX)
    @ApiModelProperty(value = "Middle name", required = true)
    private String middleName;
}
