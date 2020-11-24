package com.ecaservice.external.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

/**
 * Base response dto model.
 *
 * @param <T> - payload generic type
 */
@Data
@Builder
public class ResponseDto<T> {

    @Tolerate
    public ResponseDto() {
        //default constructor
    }

    /**
     * Response payload
     */
    @ApiModelProperty(value = "Response payload")
    private T payload;

    /**
     * Request status
     */
    @ApiModelProperty(value = "Request status")
    private RequestStatus requestStatus;

    /**
     * Error message
     */
    @ApiModelProperty(value = "Error message")
    private String errorDescription;
}
