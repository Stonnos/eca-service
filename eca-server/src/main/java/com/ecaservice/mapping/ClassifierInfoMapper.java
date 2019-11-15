package com.ecaservice.mapping;

import com.ecaservice.model.entity.ClassifierInfo;
import com.ecaservice.model.entity.ClassifierInputOptions;
import com.ecaservice.web.dto.model.ClassifierInfoDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Classifier info mapper
 *
 * @author Roman Batygin
 */
@Mapper(uses = ClassifierInputOptionsMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class ClassifierInfoMapper {

    /**
     * Maps classifier to its entity model.
     *
     * @param classifier - classifier
     * @return classifier info entity
     */
    public abstract ClassifierInfo map(Classifier classifier);

    /**
     * Maps classifier info to its dto model.
     *
     * @param classifierInfo - classifier info
     * @return classifier info dto
     */
    @Mapping(source = "classifierInputOptions", target = "inputOptions")
    public abstract ClassifierInfoDto map(ClassifierInfo classifierInfo);

    @AfterMapping
    protected void postMapping(Classifier classifier, @MappingTarget ClassifierInfo classifierInfo) {
        classifierInfo.setClassifierName(classifier.getClass().getSimpleName());
        classifierInfo.setClassifierInputOptions(newArrayList());
        if (classifier instanceof AbstractClassifier) {
            String[] options = ((AbstractClassifier) classifier).getOptions();
            for (int i = 0; i < options.length; i += 2) {
                ClassifierInputOptions classifierInputOptions = new ClassifierInputOptions();
                classifierInputOptions.setOptionName(options[i]);
                classifierInputOptions.setOptionValue(options[i + 1]);
                classifierInputOptions.setOptionOrder(i / 2);
                classifierInfo.getClassifierInputOptions().add(classifierInputOptions);
            }
        }
    }
}
