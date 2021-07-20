package com.ecaservice.ers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.Valid;
import java.util.List;

/**
 * Ensemble classifier report.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Ensemble classifier report")
public class EnsembleClassifierReport extends ClassifierReport {

    /**
     * Individual classifiers reports
     */
    @Valid
    @Schema(description = "Individual classifiers reports")
    private List<ClassifierReport> individualClassifiers;
}
