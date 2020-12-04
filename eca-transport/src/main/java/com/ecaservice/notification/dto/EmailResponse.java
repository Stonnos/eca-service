package com.ecaservice.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Email response dto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailResponse {

    /**
     * Email request id
     */
    private String requestId;
}
