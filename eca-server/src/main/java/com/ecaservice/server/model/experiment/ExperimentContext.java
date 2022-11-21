package com.ecaservice.server.model.experiment;

import com.ecaservice.server.model.entity.Experiment;
import eca.dataminer.AbstractExperiment;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.StopWatch;

/**
 * Experiment process context.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class ExperimentContext {

    /**
     * Experiment entity
     */
    private Experiment experiment;

    /**
     * Timer object
     */
    private StopWatch stopWatch;

    /**
     * Experiment history
     */
    private AbstractExperiment<?> experimentHistory;
}
