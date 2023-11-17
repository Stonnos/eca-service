package com.ecaservice.server.service.experiment;

import com.ecaservice.server.event.model.ExperimentProgressEvent;
import com.ecaservice.server.exception.experiment.ExperimentException;
import com.ecaservice.server.model.Cancelable;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.experiment.InitializationParams;
import com.ecaservice.server.service.experiment.visitor.ExperimentInitializationVisitor;
import eca.dataminer.AbstractExperiment;
import eca.dataminer.IterativeExperiment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * Experiment processing service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentProcessorService {

    private static final int PROGRESS_STEP = 2;

    private final ExperimentInitializationVisitor experimentInitializer;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Processes experiment and returns its history.
     *
     * @param experiment           - experiment {@link Experiment}
     * @param cancelable           - cancelable interface
     * @param initializationParams - experiment initialization params {@link InitializationParams}
     * @return experiment history
     */
    public AbstractExperiment<?> processExperimentHistory(Experiment experiment,
                                                          Cancelable cancelable,
                                                          InitializationParams initializationParams) {
        Assert.notNull(initializationParams, "Initialization params is not specified!");
        log.info("Starting to initialize experiment [{}]", experiment.getRequestId());
        AbstractExperiment<?> abstractExperiment =
                experiment.getExperimentType().handle(experimentInitializer, initializationParams);
        log.info("Experiment has been initialized [{}]", experiment.getRequestId());
        IterativeExperiment iterativeExperiment = abstractExperiment.getIterativeExperiment();
        int currentPercent = 0;

        log.info("Starting to process experiment [{}].", experiment.getRequestId());
        while (!cancelable.isCancelled() && iterativeExperiment.hasNext()) {
            try {
                iterativeExperiment.next();
                int percent = iterativeExperiment.getPercent();
                if (percent != currentPercent && percent % PROGRESS_STEP == 0) {
                    currentPercent = percent;
                    applicationEventPublisher.publishEvent(
                            new ExperimentProgressEvent(this, experiment, currentPercent));
                }
            } catch (Exception ex) {
                log.warn("Warning for experiment [{}]: {}", experiment.getRequestId(), ex.getMessage());
            }
        }
        if (cancelable.isCancelled()) {
            log.warn("Experiment [{}] processing has been cancelled", experiment.getRequestId());
        } else {
            log.info("Experiment [{}] processing has been finished with {} best models!",
                    experiment.getRequestId(), abstractExperiment.getHistory().size());
        }
        if (CollectionUtils.isEmpty(abstractExperiment.getHistory())) {
            throw new ExperimentException("No models has been built!");
        }
        return abstractExperiment;
    }
}
