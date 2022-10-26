package com.ecaservice.server.model.experiment;

import com.ecaservice.server.model.entity.Experiment;
import eca.dataminer.AbstractExperiment;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.StopWatch;

@Data
@Builder
public class ExperimentContext {

    private Experiment experiment;

    private StopWatch stopWatch;

    private AbstractExperiment<?> experimentHistory;
}
