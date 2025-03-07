package com.ecaservice.ers.config;

import com.ecaservice.ers.dto.EvaluationResultsStatisticsSortField;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Ers config.
 *
 * @author Roman Batygin
 */
@Data
@Validated
@ConfigurationProperties("ers")
public class ErsConfig {

    private static final int DEFAULT_MAXIMUM_PAGES_NUM = 99;

    /**
     * Optimal results size
     */
    @NotNull
    private Integer resultSize;

    /**
     * Maximum pages number
     */
    private Integer maxPagesNum = DEFAULT_MAXIMUM_PAGES_NUM;

    /**
     * Evaluation results sort fields
     */
    @NotEmpty
    private List<EvaluationResultsStatisticsSortField> evaluationResultsSortFields;
}
