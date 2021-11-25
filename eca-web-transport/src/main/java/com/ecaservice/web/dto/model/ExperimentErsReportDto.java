package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.EXPERIMENT_RESULTS_MAX_ITEMS;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LONG_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.UUID_MAX_LENGTH;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

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
    @Schema(description = "Experiment request id", example = "1d2de514-3a87-4620-9b97-c260e24340de",
            maxLength = UUID_MAX_LENGTH)
    private String experimentRequestId;

    /**
     * Total classifiers count that should be sent to ERS service
     */
    @Schema(description = "Total classifiers count", example = "10", minimum = ZERO_VALUE_STRING,
            maximum = MAX_LONG_VALUE_STRING)
    private long classifiersCount;

    /**
     * Successfully sent classifiers count
     */
    @Schema(description = "Successfully sent classifiers count", example = "10", minimum = ZERO_VALUE_STRING,
            maximum = MAX_LONG_VALUE_STRING)
    private long sentClassifiersCount;

    /**
     * Experiment results list
     */
    @ArraySchema(schema = @Schema(description = "Experiment results list"), maxItems = EXPERIMENT_RESULTS_MAX_ITEMS)
    private List<ExperimentResultsDto> experimentResults;

    /**
     * Ers report status
     */
    @Schema(description = "Ers report status")
    private EnumDto ersReportStatus;
}
