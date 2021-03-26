package com.ecaservice.notification.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Email response dto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Email response")
public class EmailResponse {

    /**
     * Email request id
     */
    @ApiModelProperty(value = "Request id")
    private String requestId;
}
