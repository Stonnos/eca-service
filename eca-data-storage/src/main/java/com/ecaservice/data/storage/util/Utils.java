package com.ecaservice.data.storage.util;

import com.ecaservice.data.storage.entity.AttributeType;
import com.ecaservice.data.storage.entity.AttributeValueEntity;
import lombok.experimental.UtilityClass;
import weka.core.Attribute;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    /**
     * Gets attribute type.
     *
     * @param attribute - attribute
     * @return attribute type
     */
    public static AttributeType getAttributeType(Attribute attribute) {
        if (attribute.isNominal()) {
            return AttributeType.NOMINAL;
        } else if (attribute.isDate()) {
            return AttributeType.DATE;
        } else if (attribute.isNumeric()) {
            return AttributeType.NUMERIC;
        } else {
            throw new IllegalArgumentException(String.format("Unexpected attribute '%s' type!", attribute.name()));
        }
    }

    /**
     * Gets attribute values entities.
     *
     * @param attribute - attribute
     * @return attribute values entities
     */
    public static List<AttributeValueEntity> getAttributeValues(Attribute attribute) {
        return IntStream.range(0, attribute.numValues())
                .mapToObj(i -> {
                    var attributeValueEntity = new AttributeValueEntity();
                    attributeValueEntity.setValue(attribute.value(i));
                    attributeValueEntity.setValueOrder(i);
                    return attributeValueEntity;
                })
                .collect(Collectors.toList());
    }
}
