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
import eca.ensemble.forests.RandomForests;
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
import org.springframework.util.CollectionUtils;
import weka.classifiers.AbstractClassifier;

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

    //@PostConstruct
    //@Transactional
    public void migrateInputOptions() {
        List<EvaluationLog> evaluationLogs = evaluationLogRepository.findLogs();
        for (EvaluationLog evaluationLog : evaluationLogs) {
            if (!CollectionUtils.isEmpty(evaluationLog.getInputOptionsMap())) {
                AbstractClassifier classifier = null;
                switch (evaluationLog.getClassifierName()) {
                    case "Logistic":
                        classifier = new Logistic();
                        break;
                    case "CART":
                        CART tree = new CART();
                        tree.setRandomTree(true);
                        tree.setUseRandomSplits(true);
                        classifier = tree;
                        break;
                    case "C45":
                        C45 c45 = new C45();
                        c45.setRandomTree(true);
                        c45.setUseRandomSplits(true);
                        classifier = c45;
                        break;
                    case "ID3":
                        ID3 id3 = new ID3();
                        id3.setRandomTree(true);
                        id3.setUseRandomSplits(true);
                        classifier = id3;
                        break;
                    case "CHAID":
                        CHAID chaid = new CHAID();
                        chaid.setRandomTree(true);
                        chaid.setUseRandomSplits(true);
                        classifier = chaid;
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
                        classifier = new RandomForests();
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

                List<ClassifierInputOptions> classifierInputOptionsList = newArrayList();
                evaluationLog.getInputOptionsMap().forEach((k, v) -> {
                    ClassifierInputOptions classifierInputOptions = new ClassifierInputOptions();
                    classifierInputOptions.setOptionName(k);
                    classifierInputOptions.setOptionValue(v);
                    int optionIdx = optionKeys.indexOf(k);
                    if (optionIdx < 0) {
                        log.warn("Warn");
                    }
                    if (optionIdx >= 0) {
                        classifierInputOptions.setOptionOrder(optionIdx);
                    }
                    classifierInputOptionsList.add(classifierInputOptions);
                });
                orderOptions(classifierInputOptionsList);
                evaluationLog.setClassifierInputOptions(classifierInputOptionsList);
                evaluationLogRepository.save(evaluationLog);
            }
        }
        log.info("Classifier input options migration has been finished!");
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
