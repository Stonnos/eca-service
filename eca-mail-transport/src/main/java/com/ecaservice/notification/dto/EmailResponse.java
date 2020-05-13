package com.ecaservice.notification.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

/**
 * Email response dto.
 */
@Data
@Builder
public class EmailResponse {

    @Tolerate
    public EmailResponse() {
    }

    /**
     * Email request id
     */
    private String requestId;
}
