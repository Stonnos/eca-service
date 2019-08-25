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
     * Experiment uuid
     */
    @ApiModelProperty(value = "Experiment uuid")
    private String experimentUuid;

    /**
     * Total classifiers count that should be sent to ERS service
     */
    @ApiModelProperty(value = "Total classifiers count")
    private long classifiersCount;

    /**
     * Successfully saved classifiers count
     */
    @ApiModelProperty(value = "Successfully saved classifiers count")
    private long successfullySavedClassifiers;

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
