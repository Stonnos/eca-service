package com.ecaservice.server.service.evaluation;


import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.model.evaluation.ClassificationResult;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationMethodVisitor;
import eca.core.evaluation.EvaluationResults;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     * @param evaluationRequest - evaluation request
     * @return classification results {@link ClassificationResult}
     */
    public ClassificationResult evaluateModel(EvaluationRequest evaluationRequest) {
        ClassificationResult classificationResult = new ClassificationResult();
        try {
            final Classifier classifier = AbstractClassifier.makeCopy(evaluationRequest.getClassifier());
            final Instances data = evaluationRequest.getData();
            final String classifierName = classifier.getClass().getSimpleName();
            log.info("Model evaluation starting for classifier = {}, data = {}, evaluationMethod = {}",
                    classifierName, data.relationName(), evaluationRequest.getEvaluationMethod());
            final Evaluation evaluation = new Evaluation(data);
            final StopWatch stopWatch = new StopWatch(String.format("Stop watching for %s", classifierName));

            evaluationRequest.getEvaluationMethod().accept(new EvaluationMethodVisitor() {
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
                    int folds = Optional.ofNullable(evaluationRequest.getNumFolds()).orElse(config.getNumFolds());
                    int tests = Optional.ofNullable(evaluationRequest.getNumTests()).orElse(config.getNumTests());
                    int seed = Optional.ofNullable(evaluationRequest.getSeed()).orElse(config.getSeed());
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
            classificationResult.setEvaluationResults(evaluationResults);
            classificationResult.setSuccess(true);
            log.info("Evaluation for model '{}' has been successfully finished!", classifierName);
            log.info(stopWatch.prettyPrint());

        } catch (Exception ex) {
            log.error("There was an error occurred in evaluation : {}", ex.getMessage());
            classificationResult.setErrorMessage(ex.getMessage());
        }
        return classificationResult;
    }
}
