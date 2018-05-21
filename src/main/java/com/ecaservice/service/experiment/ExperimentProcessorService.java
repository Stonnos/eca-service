package com.ecaservice.service.experiment;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.exception.ExperimentException;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.InitializationParams;
import com.ecaservice.service.experiment.visitor.ExperimentInitializationVisitor;
import eca.converters.model.EvaluationParams;
import eca.converters.model.ExperimentHistory;
import eca.core.evaluation.EvaluationMethod;
import eca.core.evaluation.EvaluationResults;
import eca.dataminer.AbstractExperiment;
import eca.dataminer.ClassifierComparator;
import eca.dataminer.IterativeExperiment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Experiment processing service.
 *
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
     * @param experimentInitializer - experiment initializer bean
     * @param experimentConfig      - experiment config bean
     */
    @Inject
    public ExperimentProcessorService(ExperimentInitializationVisitor experimentInitializer,
                                      ExperimentConfig experimentConfig) {
        this.experimentInitializer = experimentInitializer;
        this.experimentConfig = experimentConfig;
    }

    /**
     * Processes experiment and returns its history.
     *
     * @param experiment           experiment {@link Experiment}
     * @param initializationParams experiment initialization params {@link InitializationParams}
     * @return experiment history
     */
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
        List<EvaluationResults> evaluationResults = findBestResults(abstractExperiment.getHistory());
        log.info("Experiment {} processing has been finished with {} best models!",
                experiment.getId(), evaluationResults.size());
        return buildExperimentHistory(evaluationResults, abstractExperiment);
    }

    private ExperimentHistory buildExperimentHistory(List<EvaluationResults> evaluationResults,
                                                     AbstractExperiment abstractExperiment) {
        EvaluationParams evaluationParams = EvaluationMethod.TRAINING_DATA.equals(abstractExperiment
                .getEvaluationMethod()) ? null : new EvaluationParams(abstractExperiment.getNumFolds(),
                abstractExperiment.getNumTests());
        return new ExperimentHistory(evaluationResults, abstractExperiment.getData(), abstractExperiment
                .getEvaluationMethod(), evaluationParams);
    }

    private List<EvaluationResults> findBestResults(List<EvaluationResults> experimentHistory) {
        if (CollectionUtils.isEmpty(experimentHistory)) {
            throw new ExperimentException("No models has been built!");
        }
        experimentHistory.sort(new ClassifierComparator());
        return experimentHistory.stream().limit(
                experimentHistory.size() < experimentConfig.getResultSize() ? experimentHistory.size() :
                        experimentConfig.getResultSize()).collect(Collectors.toList());
    }
}
