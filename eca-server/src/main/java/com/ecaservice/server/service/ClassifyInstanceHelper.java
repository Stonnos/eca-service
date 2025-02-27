package com.ecaservice.server.service;

import com.ecaservice.web.dto.model.ClassifyInstanceResultDto;
import com.ecaservice.web.dto.model.ClassifyInstanceValueDto;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.ecaservice.server.util.FieldConstraints.SCALE;

/**
 * Classify instance helper.
 *
 * @author Roman Batygin
 */
@Slf4j
@UtilityClass
public class ClassifyInstanceHelper {

    /**
     * Classify instance.
     *
     * @param classifier - classifier object
     * @param data       - training data
     * @param values     - input attributes vector
     * @return classify instances result dto
     */
    @SneakyThrows
    public static ClassifyInstanceResultDto classifyInstance(Classifier classifier,
                                                             Instances data,
                                                             List<ClassifyInstanceValueDto> values) {
        Instance instance = createInstances(data, values);
        int classIndex = (int) classifier.classifyInstance(instance);
        double[] probabilities = classifier.distributionForInstance(instance);
        ClassifyInstanceResultDto classifyInstanceResultDto = new ClassifyInstanceResultDto();
        classifyInstanceResultDto.setClassIndex(classIndex);
        classifyInstanceResultDto.setClassValue(data.classAttribute().value(classIndex));
        BigDecimal probability =
                BigDecimal.valueOf(probabilities[classIndex]).setScale(SCALE, RoundingMode.HALF_UP);
        classifyInstanceResultDto.setProbability(probability);
        return classifyInstanceResultDto;
    }

    private static Instance createInstances(Instances data, List<ClassifyInstanceValueDto> values) {
        Instance instance = new DenseInstance(data.numAttributes());
        values.forEach(classifyInstanceValueDto -> {
            Attribute attribute = data.attribute(classifyInstanceValueDto.getIndex());
            instance.setValue(attribute, classifyInstanceValueDto.getValue().doubleValue());
        });
        instance.setDataset(data);
        return instance;
    }
}
