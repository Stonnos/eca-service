package com.ecaservice.ers.controller.doc;

import lombok.experimental.UtilityClass;

/**
 * Api examples utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ApiExamples {

    /**
     * Get evaluation results response json
     */
    public static final String GET_OPTIMAL_CLASSIFIER_OPTIONS_REQUEST_JSON = "{\"requestId\": " +
            "\"f8cecbf7-405b-403b-9a94-f51e8fb73ed8\", \"relationName\": \"Glass\", " +
            "\"dataHash\": \"2aeb5c41423c895995e8cb304fe30b2d\", \"evaluationMethodReport\": {\"evaluationMethod\": " +
            "\"CROSS_VALIDATION\", \"numFolds\": 10, \"numTests\": 1, \"seed\": 1}, \"sortFields\": null}";

    /**
     * Get evaluation results request json
     */
    public static final String GET_OPTIMAL_CLASSIFIER_OPTIONS_RESPONSE_JSON = "{\"requestId\": " +
            "\"f8cecbf7-405b-403b-9a94-f51e8fb73ed8\", \"classifierReports\": [{\"type\": \"ClassifierReport\", " +
            "\"classifierName\": \"ExtraTreesClassifier\", \"classifierDescription\": null, \"options\": " +
            "\"{\\\"type\\\":\\\"extra_trees\\\",\\\"numIterations\\\":20,\\\"numThreads\\\":6,\\\"seed\\\":1," +
            "\\\"numRandomAttr\\\":5,\\\"minObj\\\":2,\\\"maxDepth\\\":0,\\\"decisionTreeType\\\":\\\"ID3\\\"," +
            "\\\"numRandomSplits\\\":6,\\\"useBootstrapSamples\\\":false}\", \"classifierInputOptions\": " +
            "[{\"key\": \"Число потоков\", \"value\": \"6\"}, {\"key\": \"Формирование обучающих выборок:\", " +
            "\"value\": \"Исходное обучающее множество\"}, {\"key\": \"Алгоритм построения дерева решений:\", " +
            "\"value\": \"ID3\"}, {\"key\": \"Число деревьев:\", \"value\": \"20\"}, {\"key\": \"Максиальная " +
            "глубина дерева:\", \"value\": \"0\"}, {\"key\": \"Число случайных атрибутов:\", \"value\": \"5\"}, " +
            "{\"key\": \"Число случайных расщеплений атрибута:\", \"value\": \"6\"}, {\"key\": \"Минимальное " +
            "число объектов в листе:\", \"value\": \"2\"}, {\"key\": \"Начальное значение (Seed)\", \"value\": " +
            "\"1\"}], \"metaClassifier\": false}, {\"type\": \"ClassifierReport\", \"classifierName\": " +
            "\"ExtraTreesClassifier\", \"classifierDescription\": null, \"options\": \"{\\\"type\\\":" +
            "\\\"extra_trees\\\",\\\"numIterations\\\":20,\\\"numThreads\\\":6,\\\"seed\\\":1," +
            "\\\"numRandomAttr\\\":5,\\\"minObj\\\":2,\\\"maxDepth\\\":0,\\\"decisionTreeType\\\":" +
            "\\\"ID3\\\",\\\"numRandomSplits\\\":6,\\\"useBootstrapSamples\\\":false}\", " +
            "\"classifierInputOptions\": [{\"key\": \"Число потоков\", \"value\": \"6\"}, {\"key\": " +
            "\"Формирование обучающих выборок:\", \"value\": \"Исходное обучающее множество\"}, " +
            "{\"key\": \"Алгоритм построения дерева решений:\", \"value\": \"ID3\"}, {\"key\": " +
            "\"Число деревьев:\", \"value\": \"20\"}, {\"key\": \"Максиальная глубина дерева:\", " +
            "\"value\": \"0\"}, {\"key\": \"Число случайных атрибутов:\", \"value\": \"5\"}, " +
            "{\"key\": \"Число случайных расщеплений атрибута:\", \"value\": \"6\"}, {\"key\": " +
            "\"Минимальное число объектов в листе:\", \"value\": \"2\"}, {\"key\": \"Начальное значение (Seed)\", " +
            "\"value\": \"1\"}], \"metaClassifier\": false}]}";

    /**
     * Get optimal classifier options bad request response json
     */
    public static final String GET_OPTIMAL_CLASSIFIER_OPTIONS_BAD_REQUEST_RESPONSE_JSON =
            "[{\"fieldName\": \"evaluationMethodReport.evaluationMethod\", \"code\": \"NotNull\", " +
                    "\"errorMessage\": \"must not be null\"}, {\"fieldName\": \"dataHash\", \"code\": " +
                    "\"NotBlank\", \"errorMessage\": \"must not be blank\"}, {\"fieldName\": \"relationName\", " +
                    "\"code\": \"NotBlank\", \"errorMessage\": \"must not be blank\"}]";

    /**
     * Evaluation results request json
     */
    public static final String EVALUATION_RESULTS_REQUEST_JSON =
            "{\"requestId\": \"04b1373f-7838-4535-b65a-88c088830879\", " +
                    "\"instances\": {\"structure\": \"<data structure>\", \"relationName\": " +
                    "\"Glass\", \"numInstances\": 214, \"numAttributes\": 10, \"numClasses\": 6, " +
                    "\"className\": \"Type\"}, \"classifierReport\": {\"type\": \"ClassifierReport\", " +
                    "\"classifierName\": \"ExtraTreesClassifier\", \"classifierDescription\": null, " +
                    "\"options\": \"{\\\"type\\\":\\\"extra_trees\\\",\\\"numIterations\\\":20," +
                    "\\\"numThreads\\\":6,\\\"seed\\\":1,\\\"numRandomAttr\\\":5,\\\"minObj\\\":2," +
                    "\\\"maxDepth\\\":0,\\\"decisionTreeType\\\":\\\"ID3\\\",\\\"numRandomSplits\\\":6," +
                    "\\\"useBootstrapSamples\\\":false}\", \"classifierInputOptions\": [{\"key\": " +
                    "\"Число деревьев:\", \"value\": \"20\"}, {\"key\": \"Минимальное число объектов в листе:\", " +
                    "\"value\": \"2\"}, {\"key\": \"Максиальная глубина дерева:\", \"value\": \"0\"}, " +
                    "{\"key\": \"Число случайных атрибутов:\", \"value\": \"5\"}, {\"key\": " +
                    "\"Алгоритм построения дерева решений:\", \"value\": \"ID3\"}, {\"key\": " +
                    "\"Число потоков\", \"value\": \"6\"}, {\"key\": \"Начальное значение (Seed)\", " +
                    "\"value\": \"1\"}, {\"key\": \"Число случайных расщеплений атрибута:\", " +
                    "\"value\": \"6\"}, {\"key\": \"Формирование обучающих выборок:\", \"value\": " +
                    "\"Исходное обучающее множество\"}], \"metaClassifier\": false}, \"evaluationMethodReport\": " +
                    "{\"evaluationMethod\": \"CROSS_VALIDATION\", \"numFolds\": 10, \"numTests\": 1, " +
                    "\"seed\": 1}, \"statistics\": {\"numTestInstances\": 214, \"numCorrect\": 172, " +
                    "\"numIncorrect\": 42, \"pctCorrect\": 80.37383177570094, \"pctIncorrect\": 19.626168224299064, " +
                    "\"meanAbsoluteError\": 0.11885129227585131, \"rootMeanSquaredError\": 0.231476151125149, " +
                    "\"maxAucValue\": 0.9983739837398374, \"varianceError\": 0.00436052266530287, " +
                    "\"confidenceIntervalLowerBound\": 0.14902362368597982, \"confidenceIntervalUpperBound\": " +
                    "0.24349974080000147}, \"classificationCosts\": [{\"classValue\": \"build wind float\", " +
                    "\"truePositiveRate\": 0.9142857142857143, \"falsePositiveRate\": 0.125, \"trueNegativeRate\": " +
                    "0.875, \"falseNegativeRate\": 0.08571428571428572, \"rocCurve\": {\"aucValue\": " +
                    "0.9313988095238095, \"specificity\": 0.8611111111111112, \"sensitivity\": 0.9428571428571428, " +
                    "\"thresholdValue\": 0.4138291855203621}}, {\"classValue\": \"vehic wind float\", " +
                    "\"truePositiveRate\": 0.23529411764705882, \"falsePositiveRate\": 0.01015228426395939, " +
                    "\"trueNegativeRate\": 0.9898477157360406, \"falseNegativeRate\": 0.7647058823529411, " +
                    "\"rocCurve\": {\"aucValue\": 0.9252015527022992, \"specificity\": 0.751269035532995, " +
                    "\"sensitivity\": 1.0, \"thresholdValue\": 0.08396763793615986}}, {\"classValue\": \"tableware\", " +
                    "\"truePositiveRate\": 0.7777777777777778, \"falsePositiveRate\": 0.004878048780487805, " +
                    "\"trueNegativeRate\": 0.9951219512195122, \"falseNegativeRate\": 0.2222222222222222, " +
                    "\"rocCurve\": {\"aucValue\": 0.9983739837398374, \"specificity\": 0.9951219512195122, " +
                    "\"sensitivity\": 1.0, \"thresholdValue\": 0.33422619047619045}}, {\"classValue\": " +
                    "\"build wind non-float\", \"truePositiveRate\": 0.8289473684210527, \"falsePositiveRate\": " +
                    "0.10869565217391304, \"trueNegativeRate\": 0.8913043478260869, \"falseNegativeRate\": " +
                    "0.17105263157894737, \"rocCurve\": {\"aucValue\": 0.9111365369946606, \"specificity\": " +
                    "0.9492753623188406, \"sensitivity\": 0.75, \"thresholdValue\": 0.49067460317460315}}, " +
                    "{\"classValue\": \"headlamps\", \"truePositiveRate\": 0.8275862068965517, " +
                    "\"falsePositiveRate\": 0.016216216216216217, \"trueNegativeRate\": 0.9837837837837838, " +
                    "\"falseNegativeRate\": 0.1724137931034483, \"rocCurve\": {\"aucValue\": 0.9606710158434296, " +
                    "\"specificity\": 0.9459459459459459, \"sensitivity\": 0.9655172413793104, " +
                    "\"thresholdValue\": 0.1461273690078038}}, {\"classValue\": \"containers\", " +
                    "\"truePositiveRate\": 0.7692307692307693, \"falsePositiveRate\": 0.014925373134328358, " +
                    "\"trueNegativeRate\": 0.9850746268656716, \"falseNegativeRate\": 0.23076923076923078, " +
                    "\"rocCurve\": {\"aucValue\": 0.9862227324913893, \"specificity\": 0.9701492537313433, " +
                    "\"sensitivity\": 0.9230769230769231, \"thresholdValue\": 0.2746176338281601}}], " +
                    "\"confusionMatrix\": [{\"actualClass\": \"build wind float\", \"predictedClass\": " +
                    "\"build wind float\", \"numInstances\": 64}, {\"actualClass\": \"build wind float\", " +
                    "\"predictedClass\": \"vehic wind float\", \"numInstances\": 1}, {\"actualClass\": " +
                    "\"build wind float\", \"predictedClass\": \"tableware\", \"numInstances\": 0}, " +
                    "{\"actualClass\": \"build wind float\", \"predictedClass\": \"build wind non-float\", " +
                    "\"numInstances\": 5}, {\"actualClass\": \"build wind float\", \"predictedClass\": " +
                    "\"headlamps\", \"numInstances\": 0}, {\"actualClass\": \"build wind float\", " +
                    "\"predictedClass\": \"containers\", \"numInstances\": 0}, {\"actualClass\": " +
                    "\"vehic wind float\", \"predictedClass\": \"build wind float\", \"numInstances\": 9}, " +
                    "{\"actualClass\": \"vehic wind float\", \"predictedClass\": \"vehic wind float\", " +
                    "\"numInstances\": 4}, {\"actualClass\": \"vehic wind float\", \"predictedClass\": " +
                    "\"tableware\", \"numInstances\": 0}, {\"actualClass\": \"vehic wind float\", " +
                    "\"predictedClass\": \"build wind non-float\", \"numInstances\": 4}, {\"actualClass\": " +
                    "\"vehic wind float\", \"predictedClass\": \"headlamps\", \"numInstances\": 0}, " +
                    "{\"actualClass\": \"vehic wind float\", \"predictedClass\": \"containers\", " +
                    "\"numInstances\": 0}, {\"actualClass\": \"tableware\", \"predictedClass\": " +
                    "\"build wind float\", \"numInstances\": 0}, {\"actualClass\": \"tableware\", " +
                    "\"predictedClass\": \"vehic wind float\", \"numInstances\": 0}, {\"actualClass\": " +
                    "\"tableware\", \"predictedClass\": \"tableware\", \"numInstances\": 7}, " +
                    "{\"actualClass\": \"tableware\", \"predictedClass\": \"build wind non-float\", " +
                    "\"numInstances\": 1}, {\"actualClass\": \"tableware\", \"predictedClass\": \"headlamps\", " +
                    "\"numInstances\": 1}, {\"actualClass\": \"tableware\", \"predictedClass\": \"containers\", " +
                    "\"numInstances\": 0}, {\"actualClass\": \"build wind non-float\", \"predictedClass\": " +
                    "\"build wind float\", \"numInstances\": 8}, {\"actualClass\": \"build wind non-float\", " +
                    "\"predictedClass\": \"vehic wind float\", \"numInstances\": 1}, {\"actualClass\": " +
                    "\"build wind non-float\", \"predictedClass\": \"tableware\", \"numInstances\": 1}, " +
                    "{\"actualClass\": \"build wind non-float\", \"predictedClass\": \"build wind non-float\", " +
                    "\"numInstances\": 63}, {\"actualClass\": \"build wind non-float\", \"predictedClass\": " +
                    "\"headlamps\", \"numInstances\": 1}, {\"actualClass\": \"build wind non-float\", " +
                    "\"predictedClass\": \"containers\", \"numInstances\": 2}, {\"actualClass\": \"headlamps\", " +
                    "\"predictedClass\": \"build wind float\", \"numInstances\": 1}, {\"actualClass\": " +
                    "\"headlamps\", \"predictedClass\": \"vehic wind float\", \"numInstances\": 0}, " +
                    "{\"actualClass\": \"headlamps\", \"predictedClass\": \"tableware\", \"numInstances\": 0}, " +
                    "{\"actualClass\": \"headlamps\", \"predictedClass\": \"build wind non-float\", " +
                    "\"numInstances\": 3}, {\"actualClass\": \"headlamps\", \"predictedClass\": \"headlamps\", " +
                    "\"numInstances\": 24}, {\"actualClass\": \"headlamps\", \"predictedClass\": \"containers\", " +
                    "\"numInstances\": 1}, {\"actualClass\": \"containers\", \"predictedClass\": " +
                    "\"build wind float\", \"numInstances\": 0}, {\"actualClass\": \"containers\", " +
                    "\"predictedClass\": \"vehic wind float\", \"numInstances\": 0}, {\"actualClass\": " +
                    "\"containers\", \"predictedClass\": \"tableware\", \"numInstances\": 0}, {\"actualClass\": " +
                    "\"containers\", \"predictedClass\": \"build wind non-float\", \"numInstances\": 2}, " +
                    "{\"actualClass\": \"containers\", \"predictedClass\": \"headlamps\", \"numInstances\": 1}, " +
                    "{\"actualClass\": \"containers\", \"predictedClass\": \"containers\", \"numInstances\": 10}]}";

    /**
     * Evaluation results response json
     */
    public static final String EVALUATION_RESULTS_RESPONSE_JSON =
            "{\"requestId\": \"04b1373f-7838-4535-b65a-88c088830879\"}";

    /**
     * Evaluation results bad request response json
     */
    public static final String EVALUATION_RESULTS_BAD_REQUEST_RESPONSE_JSON =
            "[{\"fieldName\": \"requestId\", \"code\": \"Pattern\", \"errorMessage\": " +
                    "\"must match \\\"^[0-9a-f]{8}-[0-9a-f]{4}-[34][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$\\\"\"}, " +
                    "{\"fieldName\": \"instances.structure\", \"code\": \"NotBlank\", \"errorMessage\": " +
                    "\"must not be blank\"}, {\"fieldName\": \"requestId\", \"code\": \"NotBlank\", " +
                    "\"errorMessage\": \"must not be blank\"}, {\"fieldName\": \"instances.relationName\", " +
                    "\"code\": \"NotBlank\", \"errorMessage\": \"must not be blank\"}, {\"fieldName\": " +
                    "\"classifierReport.classifierName\", \"code\": \"NotBlank\", " +
                    "\"errorMessage\": \"must not be blank\"}]";

    /**
     * Get evaluation results request json
     */
    public static final String GET_EVALUATION_RESULTS_REQUEST_JSON =
            "{\"requestId\": \"04b1373f-7838-4535-b65a-88c088830879\"}";

    /**
     * Get evaluation results bad request response json
     */
    public static final String GET_EVALUATION_RESULTS_BAD_REQUEST_RESPONSE_JSON =
            "[{\"fieldName\": \"requestId\", \"code\": \"Pattern\", \"errorMessage\": " +
                    "\"must match \\\"^[0-9a-f]{8}-[0-9a-f]{4}-[34][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$\\\"\"}]";

    /**
     * Get evaluation results response json
     */
    public static final String GET_EVALUATION_RESULTS_RESPONSE_JSON =
            "{\"requestId\": \"04b1373f-7838-4535-b65a-88c088830879\", " +
                    "\"instances\": {\"structure\": \"<data structure>\", \"relationName\": \"Glass\", " +
                    "\"numInstances\": 214, \"numAttributes\": 10, \"numClasses\": 6, \"className\": \"Type\"}, " +
                    "\"classifierReport\": {\"type\": \"ClassifierReport\", \"classifierName\": " +
                    "\"ExtraTreesClassifier\", \"classifierDescription\": null, \"options\": \"{\\\"type\\\":" +
                    "\\\"extra_trees\\\",\\\"numIterations\\\":20,\\\"numThreads\\\":6,\\\"seed\\\":1," +
                    "\\\"numRandomAttr\\\":5,\\\"minObj\\\":2,\\\"maxDepth\\\":0,\\\"decisionTreeType\\\":" +
                    "\\\"ID3\\\",\\\"numRandomSplits\\\":6,\\\"useBootstrapSamples\\\":false}\", " +
                    "\"classifierInputOptions\": [{\"key\": \"Число потоков\", \"value\": \"6\"}, " +
                    "{\"key\": \"Формирование обучающих выборок:\", \"value\": " +
                    "\"Исходное обучающее множество\"}, {\"key\": " +
                    "\"Алгоритм построения дерева решений:\", \"value\": \"ID3\"}, {\"key\": " +
                    "\"Число деревьев:\", \"value\": \"20\"}, {\"key\": \"Максиальная глубина дерева:\", " +
                    "\"value\": \"0\"}, {\"key\": \"Число случайных атрибутов:\", \"value\": \"5\"}, " +
                    "{\"key\": \"Число случайных расщеплений атрибута:\", \"value\": \"6\"}, " +
                    "{\"key\": \"Минимальное число объектов в листе:\", \"value\": \"2\"}, {\"key\": " +
                    "\"Начальное значение (Seed)\", \"value\": \"1\"}], \"metaClassifier\": false}, " +
                    "\"evaluationMethodReport\": {\"evaluationMethod\": \"CROSS_VALIDATION\", " +
                    "\"numFolds\": 10, \"numTests\": 1, \"seed\": 1}, \"statistics\": " +
                    "{\"numTestInstances\": 214, \"numCorrect\": 172, \"numIncorrect\": 42, \"pctCorrect\": 80.3738, " +
                    "\"pctIncorrect\": 19.6262, \"meanAbsoluteError\": 0.1189, \"rootMeanSquaredError\": 0.2315, " +
                    "\"maxAucValue\": 0.9984, \"varianceError\": 0.0044, \"confidenceIntervalLowerBound\": 0.1490, " +
                    "\"confidenceIntervalUpperBound\": 0.2435}, \"classificationCosts\": [{\"classValue\": " +
                    "\"build wind float\", \"truePositiveRate\": 0.9143, \"falsePositiveRate\": 0.1250, " +
                    "\"trueNegativeRate\": 0.8750, \"falseNegativeRate\": 0.0857, \"rocCurve\": {\"aucValue\": 0.9314, " +
                    "\"specificity\": 0.8611, \"sensitivity\": 0.9429, \"thresholdValue\": 0.4138}}, " +
                    "{\"classValue\": \"build wind non-float\", \"truePositiveRate\": 0.8289, " +
                    "\"falsePositiveRate\": 0.1087, \"trueNegativeRate\": 0.8913, \"falseNegativeRate\": 0.1711, " +
                    "\"rocCurve\": {\"aucValue\": 0.9111, \"specificity\": 0.9493, \"sensitivity\": 0.7500, " +
                    "\"thresholdValue\": 0.4907}}, {\"classValue\": \"containers\", \"truePositiveRate\": 0.7692, " +
                    "\"falsePositiveRate\": 0.0149, \"trueNegativeRate\": 0.9851, \"falseNegativeRate\": 0.2308, " +
                    "\"rocCurve\": {\"aucValue\": 0.9862, \"specificity\": 0.9701, \"sensitivity\": 0.9231, " +
                    "\"thresholdValue\": 0.2746}}, {\"classValue\": \"headlamps\", \"truePositiveRate\": 0.8276, " +
                    "\"falsePositiveRate\": 0.0162, \"trueNegativeRate\": 0.9838, \"falseNegativeRate\": 0.1724, " +
                    "\"rocCurve\": {\"aucValue\": 0.9607, \"specificity\": 0.9459, \"sensitivity\": 0.9655, " +
                    "\"thresholdValue\": 0.1461}}, {\"classValue\": \"tableware\", \"truePositiveRate\": 0.7778, " +
                    "\"falsePositiveRate\": 0.0049, \"trueNegativeRate\": 0.9951, \"falseNegativeRate\": 0.2222, " +
                    "\"rocCurve\": {\"aucValue\": 0.9984, \"specificity\": 0.9951, \"sensitivity\": 1.0000, " +
                    "\"thresholdValue\": 0.3342}}, {\"classValue\": \"vehic wind float\", " +
                    "\"truePositiveRate\": 0.2353, \"falsePositiveRate\": 0.0102, \"trueNegativeRate\": 0.9898, " +
                    "\"falseNegativeRate\": 0.7647, \"rocCurve\": {\"aucValue\": 0.9252, \"specificity\": 0.7513, " +
                    "\"sensitivity\": 1.0000, \"thresholdValue\": 0.0840}}], \"confusionMatrix\": [{\"actualClass\": " +
                    "\"tableware\", \"predictedClass\": \"build wind float\", \"numInstances\": 0}, {\"actualClass\": " +
                    "\"build wind non-float\", \"predictedClass\": \"build wind float\", \"numInstances\": 8}, " +
                    "{\"actualClass\": \"vehic wind float\", \"predictedClass\": \"containers\", " +
                    "\"numInstances\": 0}, {\"actualClass\": \"vehic wind float\", \"predictedClass\": " +
                    "\"build wind non-float\", \"numInstances\": 4}, {\"actualClass\": \"build wind float\", " +
                    "\"predictedClass\": \"build wind non-float\", \"numInstances\": 5}, {\"actualClass\": " +
                    "\"build wind non-float\", \"predictedClass\": \"build wind non-float\", \"numInstances\": 63}, " +
                    "{\"actualClass\": \"build wind non-float\", \"predictedClass\": \"containers\", " +
                    "\"numInstances\": 2}, {\"actualClass\": \"build wind float\", \"predictedClass\": " +
                    "\"headlamps\", \"numInstances\": 0}, {\"actualClass\": \"headlamps\", \"predictedClass\": " +
                    "\"build wind float\", \"numInstances\": 1}, {\"actualClass\": \"headlamps\", " +
                    "\"predictedClass\": \"tableware\", \"numInstances\": 0}, {\"actualClass\": \"containers\", " +
                    "\"predictedClass\": \"headlamps\", \"numInstances\": 1}, {\"actualClass\": \"headlamps\", " +
                    "\"predictedClass\": \"build wind non-float\", \"numInstances\": 3}, {\"actualClass\": " +
                    "\"headlamps\", \"predictedClass\": \"containers\", \"numInstances\": 1}, {\"actualClass\": " +
                    "\"tableware\", \"predictedClass\": \"build wind non-float\", \"numInstances\": 1}, " +
                    "{\"actualClass\": \"containers\", \"predictedClass\": \"build wind non-float\", " +
                    "\"numInstances\": 2}, {\"actualClass\": \"headlamps\", \"predictedClass\": \"headlamps\", " +
                    "\"numInstances\": 24}, {\"actualClass\": \"containers\", \"predictedClass\": " +
                    "\"build wind float\", \"numInstances\": 0}, {\"actualClass\": \"vehic wind float\", " +
                    "\"predictedClass\": \"tableware\", \"numInstances\": 0}, {\"actualClass\": " +
                    "\"vehic wind float\", \"predictedClass\": \"headlamps\", \"numInstances\": 0}, " +
                    "{\"actualClass\": \"build wind float\", \"predictedClass\": \"build wind float\", " +
                    "\"numInstances\": 64}, {\"actualClass\": \"containers\", \"predictedClass\": \"containers\", " +
                    "\"numInstances\": 10}, {\"actualClass\": \"containers\", \"predictedClass\": " +
                    "\"vehic wind float\", \"numInstances\": 0}, {\"actualClass\": \"tableware\", " +
                    "\"predictedClass\": \"containers\", \"numInstances\": 0}, {\"actualClass\": " +
                    "\"build wind non-float\", \"predictedClass\": \"tableware\", \"numInstances\": 1}, " +
                    "{\"actualClass\": \"build wind float\", \"predictedClass\": \"containers\", " +
                    "\"numInstances\": 0}, {\"actualClass\": \"tableware\", \"predictedClass\": " +
                    "\"vehic wind float\", \"numInstances\": 0}, {\"actualClass\": \"build wind float\", " +
                    "\"predictedClass\": \"tableware\", \"numInstances\": 0}, {\"actualClass\": " +
                    "\"vehic wind float\", \"predictedClass\": \"vehic wind float\", \"numInstances\": 4}, " +
                    "{\"actualClass\": \"build wind float\", \"predictedClass\": \"vehic wind float\", " +
                    "\"numInstances\": 1}, {\"actualClass\": \"vehic wind float\", \"predictedClass\": " +
                    "\"build wind float\", \"numInstances\": 9}, {\"actualClass\": \"build wind non-float\", " +
                    "\"predictedClass\": \"headlamps\", \"numInstances\": 1}, {\"actualClass\": \"containers\", " +
                    "\"predictedClass\": \"tableware\", \"numInstances\": 0}, {\"actualClass\": " +
                    "\"build wind non-float\", \"predictedClass\": \"vehic wind float\", \"numInstances\": 1}, " +
                    "{\"actualClass\": \"tableware\", \"predictedClass\": \"headlamps\", \"numInstances\": 1}, " +
                    "{\"actualClass\": \"tableware\", \"predictedClass\": \"tableware\", \"numInstances\": 7}, " +
                    "{\"actualClass\": \"headlamps\", \"predictedClass\": \"vehic wind float\", \"numInstances\": 0}]}";
}
