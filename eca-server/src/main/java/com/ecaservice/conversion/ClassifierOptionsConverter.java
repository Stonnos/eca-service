package com.ecaservice.conversion;

import com.ecaservice.mapping.options.AbstractClassifierMapper;
import com.ecaservice.mapping.options.ClassifierOptionsMapper;
import com.ecaservice.model.options.AbstractHeterogeneousClassifierOptions;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.model.options.StackingOptions;
import eca.ensemble.AbstractHeterogeneousClassifier;
import eca.ensemble.ClassifiersSet;
import eca.ensemble.EnsembleUtils;
import eca.ensemble.StackingClassifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
@Component
public class ClassifierOptionsConverter {

    private final List<AbstractClassifierMapper> classifierMappers;
    private final List<ClassifierOptionsMapper> classifierOptionsMappers;

    /**
     * Constructor with spring dependency injection.
     *
     * @param classifierMappers        - classifier mappers beans
     * @param classifierOptionsMappers - classifier options mappers beans
     */
    @Inject
    public ClassifierOptionsConverter(List<AbstractClassifierMapper> classifierMappers,
                                      List<ClassifierOptionsMapper> classifierOptionsMappers) {
        this.classifierMappers = classifierMappers;
        this.classifierOptionsMappers = classifierOptionsMappers;
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
                        AbstractHeterogeneousClassifier heterogeneousClassifier =
                                (AbstractHeterogeneousClassifier) classifier;
                        AbstractHeterogeneousClassifierOptions heterogeneousClassifierOptions =
                                (AbstractHeterogeneousClassifierOptions) classifierOptions;
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
        throw new UnsupportedOperationException(
                String.format("Can not convert '%s' classifier!", classifier.getClass().getSimpleName()));
    }

    /**
     * Converts classifier options model to classifier model.
     *
     * @param classifierOptions - classifier options object
     * @return classifier model
     */
    public AbstractClassifier convert(ClassifierOptions classifierOptions) {
        for (ClassifierOptionsMapper classifierOptionsMapper : classifierOptionsMappers) {
            if (classifierOptionsMapper.canMap(classifierOptions)) {
                AbstractClassifier classifier = classifierOptionsMapper.map(classifierOptions);
                if (classifierOptions instanceof AbstractHeterogeneousClassifierOptions) {
                    AbstractHeterogeneousClassifier heterogeneousClassifier =
                            (AbstractHeterogeneousClassifier) classifier;
                    AbstractHeterogeneousClassifierOptions heterogeneousClassifierOptions =
                            (AbstractHeterogeneousClassifierOptions) classifierOptions;
                    heterogeneousClassifier.setClassifiersSet(
                            convertClassifiersOptions(heterogeneousClassifierOptions.getClassifierOptions()));
                } else if (classifierOptions instanceof StackingOptions) {
                    StackingOptions stackingOptions = (StackingOptions) classifierOptions;
                    StackingClassifier stackingClassifier = (StackingClassifier) classifier;
                    stackingClassifier.setClassifiers(
                            convertClassifiersOptions(stackingOptions.getClassifierOptions()));
                    stackingClassifier.setMetaClassifier(convert(stackingOptions.getMetaClassifierOptions()));
                }
                return classifier;
            }
        }
        throw new UnsupportedOperationException(String.format("Can not convert '%s' classifier options!",
                classifierOptions.getClass().getSimpleName()));
    }

    private List<ClassifierOptions> convertClassifiersSet(ClassifiersSet classifiers) {
        List<ClassifierOptions> classifierOptions = new ArrayList<>(classifiers.size());
        classifiers.forEach(classifier -> classifierOptions.add(convert((AbstractClassifier) classifier)));
        return classifierOptions;
    }

    private ClassifiersSet convertClassifiersOptions(List<ClassifierOptions> classifierOptions) {
        ClassifiersSet classifiers = new ClassifiersSet();
        classifierOptions.forEach(options -> classifiers.addClassifier(convert(options)));
        return classifiers;
    }
}
