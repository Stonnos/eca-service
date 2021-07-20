package com.ecaservice.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Email response dto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Email response")
public class EmailResponse {

    /**
     * Email request id
     */
    @Schema(description = "Request id")
    private String requestId;
}
