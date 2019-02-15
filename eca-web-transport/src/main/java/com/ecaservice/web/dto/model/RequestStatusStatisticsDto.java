package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Request status statistics dto model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Requests statuses statistics model")
public class RequestStatusStatisticsDto {

    /**
     * Total requests count
     */
    @ApiModelProperty(value = "Total requests count")
    private long totalCount;

    /**
     * Requests count with NEW status
     */
    @ApiModelProperty(value = "Total requests count with status NEW")
    private long newRequestsCount;

    /**
     * Requests count with FINISHED status
     */
    @ApiModelProperty(value = "Total requests count with status FINISHED")
    private long finishedRequestsCount;

    /**
     * Requests count with TIMEOUT status
     */
    @ApiModelProperty(value = "Total requests count with status TIMEOUT")
    private long timeoutRequestsCount;

    /**
     * Requests count with ERROR status
     */
    @ApiModelProperty(value = "Total requests count with status ERROR")
    private long errorRequestsCount;
}
