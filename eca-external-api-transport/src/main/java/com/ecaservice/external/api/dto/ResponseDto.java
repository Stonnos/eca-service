package com.ecaservice.external.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

import static com.ecaservice.external.api.dto.Constraints.MAX_LENGTH_255;

/**
 * Base response dto model.
 *
 * @param <T> - payload generic type
 */
@Data
@Builder
@Schema(description = "Response model")
public class ResponseDto<T> implements Serializable {

    @Tolerate
    public ResponseDto() {
        //default constructor
    }

    /**
     * Response payload
     */
    @Schema(description = "Response payload")
    private T payload;

    /**
     * Response code
     */
    @Schema(description = "Response code", example = "SUCCESS", maxLength = MAX_LENGTH_255)
    private ResponseCode responseCode;

    /**
     * Error message
     */
    @Schema(description = "Error message", maxLength = MAX_LENGTH_255)
    private String errorDescription;
}
