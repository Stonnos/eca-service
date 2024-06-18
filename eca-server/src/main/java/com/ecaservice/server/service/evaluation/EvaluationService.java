package com.ecaservice.server.service.evaluation;

import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.exception.EvaluationException;
import com.ecaservice.server.model.evaluation.EvaluationInputDataModel;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationMethodVisitor;
import eca.core.evaluation.EvaluationResults;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.micrometer.tracing.annotation.NewSpan;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.Optional;

/**
 * Implements classifier model evaluation.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final CrossValidationConfig config;

    /**
     * Evaluates classifier model. For evaluation classifier's copy is created.
     * If options for cross - validation method is not specified then the options
     * from configs will be used for evaluation.
     *
     * @param evaluationInputDataModel - evaluation input options data model
     * @return evaluation results
     */
    @NewSpan
    public EvaluationResults evaluateModel(EvaluationInputDataModel evaluationInputDataModel) {
        try {
            final Classifier classifier = AbstractClassifier.makeCopy(evaluationInputDataModel.getClassifier());
            final Instances data = evaluationInputDataModel.getData();
            final String classifierName = classifier.getClass().getSimpleName();
            log.info("Model evaluation starting for classifier = {}, data = {}, evaluationMethod = {}",
                    classifierName, data.relationName(), evaluationInputDataModel.getEvaluationMethod());
            final Evaluation evaluation = new Evaluation(data);
            final StopWatch stopWatch = new StopWatch(String.format("Stop watching for %s", classifierName));

            evaluationInputDataModel.getEvaluationMethod().accept(new EvaluationMethodVisitor() {
                @Override
                public void evaluateModel() throws Exception {
                    stopWatch.start(String.format("%s model training", classifierName));
                    classifier.buildClassifier(data);
                    stopWatch.stop();
                    stopWatch.start(String.format("%s model evaluation", classifierName));
                    evaluation.evaluateModel(classifier, data);
                    stopWatch.stop();
                }

                @Override
                public void crossValidateModel() throws Exception {
                    int folds = Optional.ofNullable(evaluationInputDataModel.getNumFolds()).orElse(config.getNumFolds());
                    int tests = Optional.ofNullable(evaluationInputDataModel.getNumTests()).orElse(config.getNumTests());
                    int seed = Optional.ofNullable(evaluationInputDataModel.getSeed()).orElse(config.getSeed());
                    log.trace("evaluateModel: numFolds = {}, numTests = {}, seed {}", folds, tests, seed);
                    stopWatch.start(String.format("%s model evaluation", classifierName));
                    evaluation.kCrossValidateModel(AbstractClassifier.makeCopy(classifier), data, folds, tests, seed);
                    stopWatch.stop();

                    stopWatch.start(String.format("%s model training", classifierName));
                    classifier.buildClassifier(data);
                    stopWatch.stop();
                }
            });
            evaluation.setTotalTimeMillis(stopWatch.getTotalTimeMillis());
            EvaluationResults evaluationResults = new EvaluationResults(classifier, evaluation);
            log.info("Evaluation for model '{}' has been successfully finished!", classifierName);
            log.info(stopWatch.prettyPrint());
            return evaluationResults;
        } catch (Exception ex) {
            throw new EvaluationException(ex.getMessage());
        }
    }
}
