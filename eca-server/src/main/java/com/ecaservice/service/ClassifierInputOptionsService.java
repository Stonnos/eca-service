package com.ecaservice.service;

import com.ecaservice.model.entity.ClassifierInputOptions;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.repository.EvaluationLogRepository;
import eca.ensemble.AbstractHeterogeneousClassifier;
import eca.ensemble.AdaBoostClassifier;
import eca.ensemble.ClassifiersSet;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.ModifiedHeterogeneousClassifier;
import eca.ensemble.RandomNetworks;
import eca.ensemble.StackingClassifier;
import eca.ensemble.forests.ExtraTreesClassifier;
import eca.metrics.KNearestNeighbours;
import eca.neural.NeuralNetwork;
import eca.regression.Logistic;
import eca.trees.C45;
import eca.trees.CART;
import eca.trees.CHAID;
import eca.trees.ID3;
import eca.trees.J48;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.trees.RandomForest;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ClassifierInputOptionsService {

    private final EvaluationLogRepository evaluationLogRepository;

    @Inject
    public ClassifierInputOptionsService(EvaluationLogRepository evaluationLogRepository) {
        this.evaluationLogRepository = evaluationLogRepository;
    }

    @PostConstruct
    @Transactional
    public void migrateInputOptions() throws Exception {
        for (EvaluationLog evaluationLog : evaluationLogRepository.findAll()) {
            if (!CollectionUtils.isEmpty(evaluationLog.getInputOptionsMap())) {
                // try {
                AbstractClassifier classifier = null;

                // } catch (Exception ex) {
                //    log.error("Error for classifier {}: {}", evaluationLog.getClassifierName(), ex.getMessage());
                //}
                switch (evaluationLog.getClassifierName()) {
                    case "Logistic":
                        classifier = new Logistic();
                        break;
                    case "CART":
                        classifier = new CART();
                        break;
                    case "C45":
                        classifier = new C45();
                        break;
                    case "ID3":
                        classifier = new ID3();
                        break;
                    case "CHAID":
                        classifier = new CHAID();
                        break;
                    case "J48":
                        classifier = new J48();
                        break;
                    case "KNearestNeighbours":
                        classifier = new KNearestNeighbours();
                        break;
                    case "NeuralNetwork":
                        classifier = new NeuralNetwork();
                        break;
                    case "AdaBoostClassifier":
                        classifier = new AdaBoostClassifier();
                        break;
                    case "HeterogeneousClassifier":
                        classifier = new HeterogeneousClassifier();
                        break;
                    case "ModifiedHeterogeneousClassifier":
                        classifier = new ModifiedHeterogeneousClassifier();
                        break;
                    case "RandomForests":
                        classifier = new RandomForest();
                        break;
                    case "RandomNetworks":
                        classifier = new RandomNetworks();
                        break;
                    case "ExtraTreesClassifier":
                        classifier = new ExtraTreesClassifier();
                        break;
                    case "StackingClassifier":
                        classifier = new StackingClassifier();
                        break;
                    default:
                        throw new IllegalArgumentException(
                                String.format("Can not handle %s", evaluationLog.getClassifierName()));
                }
                if (classifier instanceof StackingClassifier) {
                    StackingClassifier stackingClassifier = (StackingClassifier) classifier;
                    stackingClassifier.setMetaClassifier(new CART());
                    stackingClassifier.setClassifiers(new ClassifiersSet());
                    for (int i = 0; i < 10; i++) {
                        stackingClassifier.getClassifiers().addClassifier(new CART());
                    }
                } else if (classifier instanceof AbstractHeterogeneousClassifier) {
                    AbstractHeterogeneousClassifier heterogeneousClassifier =
                            (AbstractHeterogeneousClassifier) classifier;
                    heterogeneousClassifier.setClassifiersSet(new ClassifiersSet());
                    for (int i = 0; i < 10; i++) {
                        heterogeneousClassifier.getClassifiersSet().addClassifier(new CART());
                    }
                }

                List<String> optionKeys = new ArrayList<>();
                String[] options = classifier.getOptions();
                for (int i = 0; i < options.length; i += 2) {
                    optionKeys.add(options[i]);
                }

                evaluationLog.setClassifierInputOptions(newArrayList());
                evaluationLog.getInputOptionsMap().forEach((k, v) -> {
                    ClassifierInputOptions classifierInputOptions = new ClassifierInputOptions();
                    classifierInputOptions.setOptionName(k);
                    classifierInputOptions.setOptionValue(v);
                    int optionIdx = optionKeys.indexOf(k);
                    if (optionIdx >= 0) {
                        classifierInputOptions.setOptionOrder(optionIdx);
                    }
                    evaluationLog.getClassifierInputOptions().add(classifierInputOptions);
                });
                orderOptions(evaluationLog.getClassifierInputOptions());
                evaluationLogRepository.save(evaluationLog);
            }
        }
    }

    private void orderOptions(List<ClassifierInputOptions> inputOptions) {
        inputOptions.sort(Comparator.comparing(ClassifierInputOptions::getOptionOrder,
                Comparator.nullsLast(Comparator.naturalOrder())));
        for (int i = 0; i < inputOptions.size(); i++) {
            ClassifierInputOptions options = inputOptions.get(i);
            if (options.getOptionOrder() != null && !options.getOptionOrder().equals(i)) {
                options.setOptionOrder(i);
            }
        }
    }
}
