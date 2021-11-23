package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LONG_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

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
    @Schema(description = "Total requests count", example = "100", minimum = ZERO_VALUE_STRING,
            maximum = MAX_LONG_VALUE_STRING)
    private long totalCount;

    /**
     * Requests count with NEW status
     */
    @Schema(description = "Total requests count with status NEW", example = "0", minimum = ZERO_VALUE_STRING,
            maximum = MAX_LONG_VALUE_STRING)
    private long newRequestsCount;

    /**
     * Requests count with IN_PROGRESS status
     */
    @Schema(description = "Total requests count with status IN_PROGRESS", example = "1", minimum = ZERO_VALUE_STRING,
            maximum = MAX_LONG_VALUE_STRING)
    private long inProgressRequestsCount;

    /**
     * Requests count with FINISHED status
     */
    @Schema(description = "Total requests count with status FINISHED", example = "99", minimum = ZERO_VALUE_STRING,
            maximum = MAX_LONG_VALUE_STRING)
    private long finishedRequestsCount;

    /**
     * Requests count with TIMEOUT status
     */
    @Schema(description = "Total requests count with status TIMEOUT", example = "0", minimum = ZERO_VALUE_STRING,
            maximum = MAX_LONG_VALUE_STRING)
    private long timeoutRequestsCount;

    /**
     * Requests count with ERROR status
     */
    @Schema(description = "Total requests count with status ERROR", example = "0", minimum = ZERO_VALUE_STRING,
            maximum = MAX_LONG_VALUE_STRING)
    private long errorRequestsCount;
}
