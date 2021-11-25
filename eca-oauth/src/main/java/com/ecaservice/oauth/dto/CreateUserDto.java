package com.ecaservice.oauth.dto;

import com.ecaservice.oauth.validation.annotations.UniqueEmail;
import com.ecaservice.oauth.validation.annotations.UniqueLogin;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.ecaservice.oauth.util.FieldConstraints.EMAIL_MAX_SIZE;
import static com.ecaservice.oauth.util.FieldConstraints.EMAIL_REGEX;
import static com.ecaservice.oauth.util.FieldConstraints.LOGIN_MAX_LENGTH;
import static com.ecaservice.oauth.util.FieldConstraints.LOGIN_MIN_LENGTH;
import static com.ecaservice.oauth.util.FieldConstraints.LOGIN_REGEX;
import static com.ecaservice.oauth.util.FieldConstraints.PERSON_NAME_MAX_SIZE;
import static com.ecaservice.oauth.util.FieldConstraints.PERSON_NAME_MIN_SIZE;
import static com.ecaservice.oauth.util.FieldConstraints.PERSON_NAME_REGEX;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1;

/**
 * Create user dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Create user model")
public class CreateUserDto {

    /**
     * User login
     */
    @NotBlank
    @Size(min = LOGIN_MIN_LENGTH, max = LOGIN_MAX_LENGTH)
    @Pattern(regexp = LOGIN_REGEX)
    @UniqueLogin
    @Schema(description = "User login", example = "user", required = true)
    private String login;

    /**
     * User email
     */
    @Email(regexp = EMAIL_REGEX)
    @Size(min = VALUE_1, max = EMAIL_MAX_SIZE)
    @UniqueEmail
    @Schema(description = "User email", example = "bat1238@yandex.ru", required = true)
    private String email;

    /**
     * First name
     */
    @NotBlank
    @Size(min = PERSON_NAME_MIN_SIZE, max = PERSON_NAME_MAX_SIZE)
    @Pattern(regexp = PERSON_NAME_REGEX)
    @Schema(description = "First name", example = "Roman", required = true)
    private String firstName;

    /**
     * Last name
     */
    @NotBlank
    @Size(min = PERSON_NAME_MIN_SIZE, max = PERSON_NAME_MAX_SIZE)
    @Pattern(regexp = PERSON_NAME_REGEX)
    @Schema(description = "Last name", example = "Batygin", required = true)
    private String lastName;

    /**
     * Middle name
     */
    @NotBlank
    @Size(min = PERSON_NAME_MIN_SIZE, max = PERSON_NAME_MAX_SIZE)
    @Pattern(regexp = PERSON_NAME_REGEX)
    @Schema(description = "Middle name", example = "Igorevich", required = true)
    private String middleName;
}
