package com.ecaservice.service;

import com.ecaservice.mapping.AbstractClassifierMapper;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.model.options.HeterogeneousClassifierOptions;
import com.ecaservice.model.options.StackingOptions;
import eca.ensemble.AbstractHeterogeneousClassifier;
import eca.ensemble.ClassifiersSet;
import eca.ensemble.EnsembleUtils;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.StackingClassifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.classifiers.AbstractClassifier;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements classifier options conversion.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ClassifierOptionsService {

    private final List<AbstractClassifierMapper> classifierMappers;

    /**
     * Constructor with spring dependency injection.
     *
     * @param classifierMappers - classifier mappers beans
     */
    @Inject
    public ClassifierOptionsService(List<AbstractClassifierMapper> classifierMappers) {
        this.classifierMappers = classifierMappers;
    }

    /**
     * Converts classifier model to its input options model.
     *
     * @param classifier - classifier object
     * @return classifiers options model
     */
    public ClassifierOptions convert(AbstractClassifier classifier) {
        for (AbstractClassifierMapper classifierMapper : classifierMappers) {
            if (classifierMapper.canMap(classifier)) {
                ClassifierOptions classifierOptions = classifierMapper.map(classifier);
                if (EnsembleUtils.isHeterogeneousEnsembleClassifier(classifier)) {
                    if (classifier instanceof AbstractHeterogeneousClassifier) {
                        HeterogeneousClassifier heterogeneousClassifier = (HeterogeneousClassifier) classifier;
                        HeterogeneousClassifierOptions heterogeneousClassifierOptions =
                                (HeterogeneousClassifierOptions) classifierOptions;
                        heterogeneousClassifierOptions.setClassifierOptions(
                                convertClassifiersSet(heterogeneousClassifier.getClassifiersSet()));
                    } else if (classifier instanceof StackingClassifier) {
                        StackingOptions stackingOptions = (StackingOptions) classifierOptions;
                        StackingClassifier stackingClassifier = (StackingClassifier) classifier;
                        stackingOptions.setMetaClassifierOptions(
                                convert((AbstractClassifier) stackingClassifier.getMetaClassifier()));
                        stackingOptions.setClassifierOptions(
                                convertClassifiersSet(stackingClassifier.getClassifiers()));
                    }
                }
                return classifierOptions;
            }
        }
        throw new IllegalArgumentException(
                String.format("Can not convert '%s' classifier!", classifier.getClass().getSimpleName()));
    }

    private List<ClassifierOptions> convertClassifiersSet(ClassifiersSet classifiers) {
        List<ClassifierOptions> classifierOptions = new ArrayList<>(classifiers.size());
        classifiers.forEach(classifier -> classifierOptions.add(convert((AbstractClassifier) classifier)));
        return classifierOptions;
    }
}
