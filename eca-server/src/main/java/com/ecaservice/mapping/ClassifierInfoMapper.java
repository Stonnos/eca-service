package com.ecaservice.mapping;

import com.ecaservice.model.entity.ClassifierInfo;
import com.ecaservice.model.entity.ClassifierInputOptions;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import weka.classifiers.AbstractClassifier;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Classifier info mapper
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class ClassifierInfoMapper {

    /**
     * Maps classifier to its entity model.
     *
     * @param classifier - classifier
     * @return classifier info entity
     */
    public abstract ClassifierInfo map(AbstractClassifier classifier);

    @AfterMapping
    protected void postMapping(AbstractClassifier classifier, @MappingTarget ClassifierInfo classifierInfo) {
        classifierInfo.setClassifierName(classifier.getClass().getSimpleName());
        classifierInfo.setClassifierInputOptions(newArrayList());
        String[] options = classifier.getOptions();
        for (int i = 0; i < options.length; i += 2) {
            ClassifierInputOptions classifierInputOptions = new ClassifierInputOptions();
            classifierInputOptions.setOptionName(options[i]);
            classifierInputOptions.setOptionValue(options[i + 1]);
            classifierInputOptions.setOptionOrder(i / 2);
            classifierInfo.getClassifierInputOptions().add(classifierInputOptions);
        }
    }
}
