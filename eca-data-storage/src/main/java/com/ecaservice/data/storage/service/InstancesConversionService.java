package com.ecaservice.data.storage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Instances conversion service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesConversionService {

    /**
     * Converts instances to data list.
     *
     * @param instances - instances object
     * @return data list
     */
    public List<List<String>> covert(Instances instances) {
        log.info("Starting to convert instances [{}]", instances.relationName());
        return instances.stream().map(this::convertInstance).collect(Collectors.toList());
    }

    private List<String> convertInstance(Instance instance) {
        return IntStream.range(0, instance.numAttributes()).mapToObj(i -> {
            String value = null;
            if (!instance.isMissing(i)) {
                Attribute attribute = instance.attribute(i);
                switch (attribute.type()) {
                    case Attribute.DATE:
                    case Attribute.NOMINAL:
                        value = instance.stringValue(i);
                        break;
                    case Attribute.NUMERIC:
                        value = String.valueOf(instance.value(i));
                        break;
                    default:
                        throw new IllegalStateException(
                                String.format("Unexpected attribute [%s] type: %d!", attribute.name(),
                                        attribute.type()));
                }
            }
            return value;
        }).collect(Collectors.toList());
    }
}
