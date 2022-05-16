package com.ecaservice.server.mapping;

import com.ecaservice.server.model.entity.ClassifierInfo;
import com.ecaservice.server.model.entity.ClassifierInputOptions;
import com.ecaservice.web.dto.model.ClassifierInfoDto;
import com.ecaservice.web.dto.model.InputOptionDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.CollectionUtils;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    protected void postMappingOptions(ClassifierInfo classifierInfo,
                                      @MappingTarget ClassifierInfoDto classifierInfoDto) {
        if (!CollectionUtils.isEmpty(classifierInfo.getClassifierInputOptions())) {
            List<InputOptionDto> sortedOptions = classifierInfo.getClassifierInputOptions()
                    .stream()
                    .sorted(Comparator.comparing(ClassifierInputOptions::getOptionOrder))
                    .map(classifierInputOptions -> new InputOptionDto(classifierInputOptions.getOptionName(),
                            classifierInputOptions.getOptionValue()))
                    .collect(Collectors.toList());
            classifierInfoDto.setInputOptions(sortedOptions);
        }
    }

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
