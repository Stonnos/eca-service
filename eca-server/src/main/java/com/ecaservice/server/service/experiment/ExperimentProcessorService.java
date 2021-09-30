package com.ecaservice.server.service.experiment;

import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.event.model.ExperimentProgressEvent;
import com.ecaservice.server.exception.experiment.ExperimentException;
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
    private final ExperimentConfig experimentConfig;

    /**
     * Processes experiment and returns its history.
     *
     * @param experiment           experiment {@link Experiment}
     * @param initializationParams experiment initialization params {@link InitializationParams}
     * @return experiment history
     */
    public AbstractExperiment<?> processExperimentHistory(Experiment experiment,
                                                          InitializationParams initializationParams) {
        Assert.notNull(initializationParams, "Initialization params is not specified!");
        log.info("Starting to initialize experiment [{}]", experiment.getRequestId());
        AbstractExperiment<?> abstractExperiment =
                experiment.getExperimentType().handle(experimentInitializer, initializationParams);
        log.info("Experiment has been initialized [{}]", experiment.getRequestId());
        IterativeExperiment iterativeExperiment = abstractExperiment.getIterativeExperiment();
        int currentPercent = 0;

        log.info("Starting to process experiment [{}].", experiment.getRequestId());
        while (iterativeExperiment.hasNext()) {
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
        postProcessFinalResults(abstractExperiment);
        log.info("Experiment [{}] processing has been finished with {} best models!",
                experiment.getRequestId(), abstractExperiment.getHistory().size());
        return abstractExperiment;
    }

    private void postProcessFinalResults(AbstractExperiment<?> abstractExperiment) {
        if (CollectionUtils.isEmpty(abstractExperiment.getHistory())) {
            throw new ExperimentException("No models has been built!");
        }
        int resultsSize = Integer.min(abstractExperiment.getHistory().size(), experimentConfig.getResultSize());
        abstractExperiment.sortByBestResults();
        abstractExperiment.reduce(resultsSize);
    }
}
