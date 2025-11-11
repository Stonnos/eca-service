package com.ecaservice.server.util;

import com.ecaservice.server.model.data.AttributeMetaInfo;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * Attribute utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class AttributeUtils {

    /**
     * Gets class attribute.
     *
     * @param attributes - attributes list
     * @param className  - class name
     * @return class attribute info
     */
    public static AttributeMetaInfo getClassAttribute(List<AttributeMetaInfo> attributes, String className) {
        return attributes.stream()
                .filter(attributeMetaInfo -> attributeMetaInfo.getName().equals(className))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException(String.format("Can't find class [%s] attribute", className)));
    }
}
