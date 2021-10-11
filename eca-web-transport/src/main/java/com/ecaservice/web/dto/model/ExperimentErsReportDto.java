package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * Experiment ERS report dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Experiment ERS report model")
public class ExperimentErsReportDto {

    /**
     * Experiment request id
     */
    @Schema(description = "Experiment request id", example = "1d2de514-3a87-4620-9b97-c260e24340de")
    private String experimentRequestId;

    /**
     * Total classifiers count that should be sent to ERS service
     */
    @Schema(description = "Total classifiers count", example = "10")
    private long classifiersCount;

    /**
     * Successfully sent classifiers count
     */
    @Schema(description = "Successfully sent classifiers count", example = "10")
    private long sentClassifiersCount;

    /**
     * Experiment results list
     */
    @Schema(description = "Experiment results list")
    private List<ExperimentResultsDto> experimentResults;

    /**
     * Ers report status
     */
    @Schema(description = "Ers report status")
    private EnumDto ersReportStatus;
}
