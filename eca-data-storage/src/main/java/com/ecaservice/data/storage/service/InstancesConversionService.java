package com.ecaservice.data.storage.service;

import com.ecaservice.web.dto.model.InstancesDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

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
     * Converts instances to instances data dto.
     *
     * @param instances - instances object
     * @return instances data dto
     */
    public InstancesDataDto covert(Instances instances) {
        var attributes = convertAttributes(instances);
        var rows = instances.stream().map(this::convertInstance).collect(Collectors.toList());
        return InstancesDataDto.builder()
                .attributes(attributes)
                .rows(rows)
                .build();
    }

    private List<String> convertAttributes(Instances data) {
        List<String> attributes = new ArrayList<>();
        for (var enumeration = data.enumerateAttributes(); enumeration.hasMoreElements();) {
            attributes.add(enumeration.nextElement().name());
        }
        return attributes;
    }

    private List<String> convertInstance(Instance instance) {
        List<String> values = newArrayList();
        for (int i = 0; i < instance.numAttributes(); i++) {
            String value = StringUtils.EMPTY;
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
            values.add(value);
        }
        return values;
    }
}
