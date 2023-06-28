package com.ecaservice.classifier.options.adapter;

import com.ecaservice.classifier.options.config.ClassifiersOptionsConfig;
import com.ecaservice.classifier.options.mapping.AbstractClassifierMapper;
import com.ecaservice.classifier.options.mapping.ClassifierOptionsMapper;
import com.ecaservice.classifier.options.model.AbstractHeterogeneousClassifierOptions;
import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.classifier.options.model.StackingOptions;
import eca.core.DecimalFormatHandler;
import eca.ensemble.AbstractHeterogeneousClassifier;
import eca.ensemble.ClassifiersSet;
import eca.ensemble.EnsembleUtils;
import eca.ensemble.StackingClassifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import weka.classifiers.AbstractClassifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements classifier options adapter class.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class ClassifierOptionsAdapter {

    private final ClassifiersOptionsConfig classifiersOptionsConfig;
    private final List<AbstractClassifierMapper> classifierMappers;
    private final List<ClassifierOptionsMapper> classifierOptionsMappers;

    /**
     * Converts classifier model to its input options model.
     *
     * @param classifier - classifier object
     * @return classifiers options model
     */
    public ClassifierOptions convert(AbstractClassifier classifier) {
        AbstractClassifierMapper classifierMapper = classifierMappers.stream()
                .filter(mapper -> mapper.canMap(classifier))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Can not convert '%s' classifier!", classifier.getClass().getSimpleName())));
        ClassifierOptions classifierOptions = classifierMapper.map(classifier);
        populateEnsembleClassifierOptions(classifier, classifierOptions);
        return classifierOptions;
    }

    /**
     * Converts classifier options model to classifier model.
     *
     * @param classifierOptions - classifier options object
     * @return classifier model
     */
    public AbstractClassifier convert(ClassifierOptions classifierOptions) {
        ClassifierOptionsMapper classifierOptionsMapper = classifierOptionsMappers.stream()
                .filter(mapper -> mapper.canMap(classifierOptions))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Can not convert '%s' classifier options!",
                        classifierOptions.getClass().getSimpleName())));
        AbstractClassifier classifier = classifierOptionsMapper.map(classifierOptions);
        populateDecimalFormat(classifier);
        populateEnsembleClassifier(classifierOptions, classifier);
        return classifier;
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

    private void populateDecimalFormat(AbstractClassifier classifier) {
        if (classifier instanceof DecimalFormatHandler) {
            DecimalFormatHandler decimalFormatHandler = (DecimalFormatHandler) classifier;
            decimalFormatHandler.getDecimalFormat().setMaximumFractionDigits(classifiersOptionsConfig.getMaximumFractionDigits());
        }
    }

    private void populateEnsembleClassifier(ClassifierOptions classifierOptions, AbstractClassifier classifier) {
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
    }

    private void populateEnsembleClassifierOptions(AbstractClassifier classifier, ClassifierOptions classifierOptions) {
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
    }
}
