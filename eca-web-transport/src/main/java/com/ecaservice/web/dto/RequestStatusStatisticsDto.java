package com.ecaservice.web.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Request status statistics dto model.
 *
 * @author Roman Batygin
 */
@Data
public class RequestStatusStatisticsDto {

    /**
     * Total requests count
     */
    @ApiModelProperty(notes = "Total requests count")
    private long totalCount;

    /**
     * Requests count with NEW status
     */
    @ApiModelProperty(notes = "Total requests count with status NEW")
    private long newRequestsCount;

    /**
     * Requests count with FINISHED status
     */
    @ApiModelProperty(notes = "Total requests count with status FINISHED")
    private long finishedRequestsCount;

    /**
     * Requests count with TIMEOUT status
     */
    @ApiModelProperty(notes = "Total requests count with status TIMEOUT")
    private long timeoutRequestsCount;

    /**
     * Requests count with ERROR status
     */
    @ApiModelProperty(notes = "Total requests count with status ERROR")
    private long errorRequestsCount;
}
