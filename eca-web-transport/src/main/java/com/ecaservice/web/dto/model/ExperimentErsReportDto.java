package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Experiment ERS report dto model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Experiment ERS report model")
public class ExperimentErsReportDto {

    /**
     * Experiment request id
     */
    @ApiModelProperty(value = "Experiment request id")
    private String experimentRequestId;

    /**
     * Total classifiers count that should be sent to ERS service
     */
    @ApiModelProperty(value = "Total classifiers count")
    private long classifiersCount;

    /**
     * Successfully sent classifiers count
     */
    @ApiModelProperty(value = "Successfully sent classifiers count")
    private long sentClassifiersCount;

    /**
     * Experiment results list
     */
    @ApiModelProperty(value = "Experiment results list")
    private List<ExperimentResultsDto> experimentResults;

    /**
     * Ers report status
     */
    @ApiModelProperty(value = "Ers report status")
    private EnumDto ersReportStatus;
}
