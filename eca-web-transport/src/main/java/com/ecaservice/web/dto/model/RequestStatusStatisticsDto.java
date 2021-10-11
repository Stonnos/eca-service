package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Request status statistics dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Requests statuses statistics model")
public class RequestStatusStatisticsDto {

    /**
     * Total requests count
     */
    @Schema(description = "Total requests count", example = "100")
    private long totalCount;

    /**
     * Requests count with NEW status
     */
    @Schema(description = "Total requests count with status NEW", example = "0")
    private long newRequestsCount;

    /**
     * Requests count with IN_PROGRESS status
     */
    @Schema(description = "Total requests count with status IN_PROGRESS", example = "1")
    private long inProgressRequestsCount;

    /**
     * Requests count with FINISHED status
     */
    @Schema(description = "Total requests count with status FINISHED", example = "99")
    private long finishedRequestsCount;

    /**
     * Requests count with TIMEOUT status
     */
    @Schema(description = "Total requests count with status TIMEOUT", example = "0")
    private long timeoutRequestsCount;

    /**
     * Requests count with ERROR status
     */
    @Schema(description = "Total requests count with status ERROR", example = "0")
    private long errorRequestsCount;
}
