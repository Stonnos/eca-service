package com.ecaservice.service.evaluation;


import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.model.InputData;
import com.ecaservice.model.evaluation.ClassificationResult;
import com.ecaservice.model.evaluation.EvaluationOption;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationMethod;
import eca.core.evaluation.EvaluationMethodVisitor;
import eca.core.evaluation.EvaluationResults;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

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
     * @param inputData            - input data {@link InputData}
     * @param evaluationMethod     - evaluation method
     * @param evaluationOptionsMap - evaluation options map
     * @return classification results {@link ClassificationResult}
     */
    public ClassificationResult evaluateModel(final InputData inputData,
                                              final EvaluationMethod evaluationMethod,
                                              final Map<EvaluationOption, String> evaluationOptionsMap) {
        ClassificationResult classificationResult = new ClassificationResult();
        try {
            final Classifier classifier = AbstractClassifier.makeCopy(inputData.getClassifier());
            final Instances data = inputData.getData();
            final String classifierName = classifier.getClass().getSimpleName();
            final Map<EvaluationOption, String> optionsMap =
                    Optional.ofNullable(evaluationOptionsMap).orElse(Collections.emptyMap());
            log.info("Model evaluation starting for classifier = {}, data = {}, evaluationMethod = {}",
                    classifierName, data.relationName(), evaluationMethod);
            final Evaluation evaluation = new Evaluation(data);
            final StopWatch stopWatch = new StopWatch(String.format("Stop watching for %s", classifierName));

            evaluationMethod.accept(new EvaluationMethodVisitor() {
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
                    int folds = NumberUtils.toInt(optionsMap.get(EvaluationOption.NUM_FOLDS), config.getNumFolds());
                    int tests = NumberUtils.toInt(optionsMap.get(EvaluationOption.NUM_TESTS), config.getNumTests());
                    int seed = NumberUtils.toInt(optionsMap.get(EvaluationOption.SEED), config.getSeed());
                    log.trace("evaluateModel: numFolds = {}, numTests = {}, seed {}", folds, tests, seed);
                    stopWatch.start(String.format("%s model evaluation", classifierName));
                    evaluation.kCrossValidateModel(AbstractClassifier.makeCopy(classifier), data, folds, tests,
                            new Random(seed));
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
            log.error("There was an error occurred in evaluation : {}", ex);
            classificationResult.setErrorMessage(ex.getMessage());
        }
        return classificationResult;
    }
}
