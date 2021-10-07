package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.DATE_TIME_PATTERN;

/**
 * User dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "User model")
public class UserDto {

    /**
     * User id
     */
    @Schema(description = "User id", example = "1")
    private Long id;

    /**
     * User login
     */
    @Schema(description = "User login", example = "admin")
    private String login;

    /**
     * User email
     */
    @Schema(description = "User email", example = "test@mail.ru")
    private String email;

    /**
     * User first name
     */
    @Schema(description = "User first name", example = "Ivan")
    private String firstName;

    /**
     * User last name
     */
    @Schema(description = "User last name", example = "Ivanov")
    private String lastName;

    /**
     * User middle name
     */
    @Schema(description = "User middle name", example = "Ivanovich")
    private String middleName;

    /**
     * User full name
     */
    @Schema(description = "User full name", example = "Ivanov Ivan Ivanovich")
    private String fullName;

    /**
     * User creation date
     */
    @Schema(description = "User creation date", type = "string", example = "2021-07-01 14:00:00")
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationDate;

    /**
     * Two factor authentication enabled?
     */
    @Schema(description = "Two factor authentication enabled")
    private boolean tfaEnabled;

    /**
     * Account locked?
     */
    @Schema(description = "Account locked")
    private boolean locked;

    /**
     * User photo id
     */
    @Schema(description = "User photo id", example = "1")
    private Long photoId;

    /**
     * Last password change date
     */
    @Schema(description = "Last password change date", type = "string", example = "2021-07-01 14:00:00")
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime passwordDate;

    /**
     * Roles list
     */
    @Schema(description = "User roles")
    private List<RoleDto> roles;
}
