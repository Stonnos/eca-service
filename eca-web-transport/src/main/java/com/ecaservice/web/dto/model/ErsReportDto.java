package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ERS report dto model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "ERS report model")
public class ErsReportDto {

    /**
     * Experiment uuid
     */
    @ApiModelProperty(value = "Experiment uuid")
    private String experimentUuid;

    /**
     * ERS requests count
     */
    @ApiModelProperty(value = "Total ERS requests count")
    private long requestsCount;

    /**
     * Successfully saved classifiers count
     */
    @ApiModelProperty(value = "Successfully saved classifiers count")
    private long successfullySavedClassifiers;

    /**
     * Failed requests count
     */
    @ApiModelProperty(value = "TFailed requests count")
    private long failedRequestsCount;

    /**
     * Ers report status
     */
    @ApiModelProperty(value = "Ers report status")
    private EnumDto ersReportStatus;
}
