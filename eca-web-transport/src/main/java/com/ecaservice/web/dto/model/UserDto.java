package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "User model")
public class UserDto {

    /**
     * User id
     */
    @ApiModelProperty(value = "User id")
    private Long id;

    /**
     * User login
     */
    @ApiModelProperty(value = "User login")
    private String login;

    /**
     * User email
     */
    @ApiModelProperty(value = "User email")
    private String email;

    /**
     * User first name
     */
    @ApiModelProperty(value = "User first name")
    private String firstName;

    /**
     * User last name
     */
    @ApiModelProperty(value = "User last name")
    private String lastName;

    /**
     * User middle name
     */
    @ApiModelProperty(value = "User middle name")
    private String middleName;

    /**
     * User creation date
     */
    @ApiModelProperty(value = "User creation date")
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationDate;

    /**
     * Two factor authentication enabled?
     */
    @ApiModelProperty(value = "Two factor authentication enabled")
    private boolean tfaEnabled;

    /**
     * User photo id
     */
    @ApiModelProperty(value = "User photo id")
    private Long photoId;

    /**
     * Last password change date
     */
    @ApiModelProperty(value = "Last password change date")
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime passwordDate;

    /**
     * Roles list
     */
    @ApiModelProperty(value = "User roles")
    private List<RoleDto> roles;
}
