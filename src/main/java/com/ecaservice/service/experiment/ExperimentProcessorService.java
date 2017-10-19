package com.ecaservice.service.experiment;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.exception.ExperimentException;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.InitializationParams;
import com.ecaservice.service.experiment.visitor.ExperimentInitializationVisitor;
import eca.converters.model.ExperimentHistory;
import eca.core.evaluation.EvaluationResults;
import eca.dataminer.AbstractExperiment;
import eca.dataminer.ClassifierComparator;
import eca.dataminer.IterativeExperiment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Experiment processing service
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ExperimentProcessorService {

    private static final int PROGRESS_STEP = 10;

    private final ExperimentInitializationVisitor experimentInitializer;
    private final ExperimentConfig experimentConfig;

    /**
     * Constructor with dependency spring injection.
     *
     * @param experimentInitializer {@link ExperimentInitializationVisitor} bean
     * @param experimentConfig      {@link ExperimentConfig} bean
     */
    @Autowired
    public ExperimentProcessorService(ExperimentInitializationVisitor experimentInitializer,
                                      ExperimentConfig experimentConfig) {
        this.experimentInitializer = experimentInitializer;
        this.experimentConfig = experimentConfig;
    }

    public ExperimentHistory processExperimentHistory(Experiment experiment,
                                                      InitializationParams initializationParams) {
        Assert.notNull(initializationParams, "Initialization params is not specified!");
        AbstractExperiment abstractExperiment =
                experiment.getExperimentType().handle(experimentInitializer, initializationParams);
        IterativeExperiment iterativeExperiment = abstractExperiment.getIterativeExperiment();
        int currentPercent = 0;

        log.info("Starting to process experiment {}.", experiment.getId());
        while (iterativeExperiment.hasNext()) {
            try {
                iterativeExperiment.next();
                int percent = iterativeExperiment.getPercent();
                if (percent != currentPercent && percent % PROGRESS_STEP == 0) {
                    log.info("Experiment {} progress: {} %.", experiment.getId(), percent);
                }
                currentPercent = percent;
            } catch (Exception ex) {
                log.warn("Warning for experiment {}: {}", experiment.getId(), ex.getMessage());
            }
        }

        List<EvaluationResults> experimentHistory = abstractExperiment.getHistory();
        experimentHistory.sort(new ClassifierComparator());
        List<EvaluationResults> evaluationResults = findBestResults(experimentHistory);
        log.info("Experiment {} processing has been finished with {} best models!",
                experiment.getId(), evaluationResults.size());
        return new ExperimentHistory(evaluationResults, abstractExperiment.getData());
    }

    private List<EvaluationResults> findBestResults(List<EvaluationResults> experimentHistory) {
        if (CollectionUtils.isEmpty(experimentHistory)) {
            throw new ExperimentException("No models was built!");
        }
        if (experimentHistory.size() < experimentConfig.getResultSize()) {
            return experimentHistory;
        } else {
            List<EvaluationResults> resultsList = new ArrayList<>(experimentConfig.getResultSize());
            for (int i = 0; i < experimentConfig.getResultSize(); i++) {
                resultsList.add(experimentHistory.get(i));
            }
            return resultsList;
        }
    }
}
