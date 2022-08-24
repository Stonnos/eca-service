package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.DATE_TIME_PATTERN;
import static com.ecaservice.web.dto.util.FieldConstraints.LOCAL_DATE_TIME_MAX_LENGTH;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LONG_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1_STRING;

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
    @Schema(description = "User id", example = "1", minimum = VALUE_1_STRING, maximum = MAX_LONG_VALUE_STRING)
    private Long id;

    /**
     * User login
     */
    @Schema(description = "User login", example = "admin", maxLength = MAX_LENGTH_255)
    private String login;

    /**
     * User email
     */
    @Schema(description = "User email", example = "test@mail.ru", maxLength = MAX_LENGTH_255)
    private String email;

    /**
     * User first name
     */
    @Schema(description = "User first name", example = "Ivan", maxLength = MAX_LENGTH_255)
    private String firstName;

    /**
     * User last name
     */
    @Schema(description = "User last name", example = "Ivanov", maxLength = MAX_LENGTH_255)
    private String lastName;

    /**
     * User middle name
     */
    @Schema(description = "User middle name", example = "Ivanovich", maxLength = MAX_LENGTH_255)
    private String middleName;

    /**
     * User full name
     */
    @Schema(description = "User full name", example = "Ivanov Ivan Ivanovich", maxLength = MAX_LENGTH_255)
    private String fullName;

    /**
     * User creation date
     */
    @Schema(description = "User creation date", type = "string", example = "2021-07-01 14:00:00",
            maxLength = LOCAL_DATE_TIME_MAX_LENGTH)
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
    @Schema(description = "User photo id", example = "1", minimum = VALUE_1_STRING, maximum = MAX_LONG_VALUE_STRING)
    private Long photoId;

    /**
     * Last password change date
     */
    @Schema(description = "Last password change date", type = "string", example = "2021-07-01 14:00:00",
            maxLength = LOCAL_DATE_TIME_MAX_LENGTH)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime passwordChangeDate;

    /**
     * Roles list
     */
    @Schema(description = "User roles")
    private List<RoleDto> roles;

    /**
     * Is user lock allowed?
     */
    @Schema(description = "Is user lock allowed?")
    private boolean lockAllowed;

    /**
     * Web pushes enabled?
     */
    @Schema(description = "Web pushes enabled")
    private boolean pushEnabled;
}
