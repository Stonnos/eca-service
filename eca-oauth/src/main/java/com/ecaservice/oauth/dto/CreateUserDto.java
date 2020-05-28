package com.ecaservice.oauth.dto;

import com.ecaservice.oauth.validation.annotations.UniqueEmail;
import com.ecaservice.oauth.validation.annotations.UniqueLogin;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.ecaservice.oauth.util.FieldConstraints.EMAIL_MAX_SIZE;
import static com.ecaservice.oauth.util.FieldConstraints.EMAIL_REGEX;
import static com.ecaservice.oauth.util.FieldConstraints.FIRST_NAME_MAX_SIZE;
import static com.ecaservice.oauth.util.FieldConstraints.FIRST_NAME_REGEX;
import static com.ecaservice.oauth.util.FieldConstraints.LOGIN_MAX_LENGTH;
import static com.ecaservice.oauth.util.FieldConstraints.LOGIN_MIN_LENGTH;
import static com.ecaservice.oauth.util.FieldConstraints.LOGIN_REGEX;

/**
 * Create user dto model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Create user model")
public class CreateUserDto {

    /**
     * User login
     */
    @NotBlank
    @Size(min = LOGIN_MIN_LENGTH, max = LOGIN_MAX_LENGTH)
    @Pattern(regexp = LOGIN_REGEX)
    @UniqueLogin
    @ApiModelProperty(value = "User login", required = true)
    private String login;

    /**
     * User email
     */
    @Email(regexp = EMAIL_REGEX)
    @Size(max = EMAIL_MAX_SIZE)
    @UniqueEmail
    @ApiModelProperty(value = "User email", required = true)
    private String email;

    /**
     * First name
     */
    @NotBlank
    @Size(max = FIRST_NAME_MAX_SIZE)
    @Pattern(regexp = FIRST_NAME_REGEX)
    @ApiModelProperty(value = "First name", required = true)
    private String firstName;
}
