package com.ecaservice.server.mapping;

import com.ecaservice.server.model.entity.ClassifierInfo;
import com.ecaservice.web.dto.model.ClassifierInfoDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import weka.classifiers.Classifier;

/**
 * Classifier info mapper
 *
 * @author Roman Batygin
 */
@Mapper
public interface ClassifierInfoMapper {

    /**
     * Maps classifier to its entity model.
     *
     * @param classifier - classifier
     * @return classifier info entity
     */
    ClassifierInfo map(Classifier classifier);

    /**
     * Maps classifier info to its dto model.
     *
     * @param classifierInfo - classifier info
     * @return classifier info dto
     */
    ClassifierInfoDto map(ClassifierInfo classifierInfo);

    @AfterMapping
    default void postMapping(Classifier classifier, @MappingTarget ClassifierInfo classifierInfo) {
        classifierInfo.setClassifierName(classifier.getClass().getSimpleName());
    }
}
