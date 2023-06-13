package com.ecaservice.data.storage.service;

import org.springframework.stereotype.Component;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Instances transformer
 *
 * @author Roman Batygin
 */
@Component
public class InstancesTransformer {

    /**
     * Transforms specified instances. Transformation includes:
     * 1. Reindexing all nominal attributes according to their order in the data table.
     * 2. Removed unused nominal attribute values
     *
     * @param sourceData - source instances
     * @return result instances
     */
    public Instances transform(Instances sourceData) {
        ArrayList<Attribute> attributes = createAttributesList(sourceData);
        Instances targetData = new Instances(sourceData.relationName(), attributes, sourceData.numInstances());
        IntStream.range(0, sourceData.numInstances()).forEach(i -> {
            Instance targetInstance = new DenseInstance(sourceData.numAttributes());
            IntStream.range(0, sourceData.numAttributes()).forEach(j -> {
                Attribute targetAttribute = targetData.attribute(j);
                Instance sourceInstances = sourceData.instance(i);
                if (sourceInstances.isMissing(j)) {
                    targetInstance.setValue(targetAttribute, weka.core.Utils.missingValue());
                } else if (targetAttribute.isNumeric()) {
                    targetInstance.setValue(targetAttribute, sourceInstances.value(j));
                } else {
                    targetInstance.setValue(targetAttribute, sourceInstances.stringValue(j));
                }
            });
            targetData.add(targetInstance);
        });
        if (sourceData.classIndex() >= 0) {
            targetData.setClassIndex(sourceData.classIndex());
        }
        return targetData;
    }

    private ArrayList<Attribute> createAttributesList(Instances data) {
        return IntStream.range(0, data.numAttributes())
                .mapToObj(i -> {
                    Attribute a = data.attribute(i);
                    if (a.isDate()) {
                        return new Attribute(a.name(), a.getDateFormat());
                    } else if (a.isNumeric()) {
                        return new Attribute(a.name());
                    } else {
                        return new Attribute(a.name(), createNominalAttributeValues(data, a));
                    }
                }).collect(Collectors.toCollection(ArrayList::new));
    }

    private List<String> createNominalAttributeValues(Instances data, Attribute attribute) {
        List<String> values = newArrayList();
        IntStream.range(0, data.numInstances()).forEach(i -> {
            if (!data.instance(i).isMissing(attribute)) {
                String value = data.instance(i).stringValue(attribute);
                if (!values.contains(value)) {
                    values.add(data.instance(i).stringValue(attribute));
                }
            }
        });
        return values;
    }
}
