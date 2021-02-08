package com.ecaservice.notification.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Map;

import static com.ecaservice.notification.util.FieldConstraints.EMAIL_MAX_SIZE;
import static com.ecaservice.notification.util.FieldConstraints.EMAIL_REGEX;

/**
 * Email request dto.
 */
@Data
@ApiModel(description = "Email request")
public class EmailRequest {

    /**
     * Receiver email
     */
    @Email(regexp = EMAIL_REGEX)
    @Size(max = EMAIL_MAX_SIZE)
    @ApiModelProperty(value = "Receiver email")
    private String receiver;

    /**
     * Template code
     */
    @NotBlank
    @ApiModelProperty(value = "Email template code")
    private String templateCode;

    /**
     * Email message variables
     */
    @ApiModelProperty(value = "Email templates variables")
    private Map<@NotBlank String, @NotBlank String> variables;
}
