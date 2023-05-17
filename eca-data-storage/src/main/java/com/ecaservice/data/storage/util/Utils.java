package com.ecaservice.data.storage.util;

import com.ecaservice.data.storage.entity.AttributeType;
import com.ecaservice.data.storage.entity.AttributeValueEntity;
import lombok.experimental.UtilityClass;
import weka.core.Attribute;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static eca.data.db.SqlQueryHelper.truncateStringValue;
import static eca.util.Utils.removeQuotes;

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
                    String value = getFormattedAttributeValue(attribute, i);
                    attributeValueEntity.setValue(value);
                    attributeValueEntity.setValueOrder(i);
                    return attributeValueEntity;
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets formatted attribute value.
     * Formatting includes:
     * 1. Quotes removal
     * 2. Value truncation to 255 length, if its length greater than 255 symbols
     *
     * @param attribute  - attribute
     * @param valueIndex - value index
     * @return formatted attribute value
     */
    public static String getFormattedAttributeValue(Attribute attribute, int valueIndex) {
        String val = removeQuotes(attribute.value(valueIndex));
        return truncateStringValue(val);
    }
}
