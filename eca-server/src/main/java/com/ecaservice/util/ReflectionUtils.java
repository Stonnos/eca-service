package com.ecaservice.util;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ConcurrentHashMap;

import static com.ecaservice.util.Utils.splitByPointSeparator;
import static org.springframework.util.ReflectionUtils.doWithFields;

/**
 * Reflection utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ReflectionUtils {

    private static final ConcurrentHashMap<String, Class<?>> fieldClassMap = new ConcurrentHashMap<>(256);

    /**
     * Gets field type. Note: Field name can be composite, for example:
     * <pre>
     *  entity.entity1.prop1
     * </pre>
     * If the field name is composite, then the last property is taken. In our case the last property is prop1.
     *
     * @param fieldName - field name
     * @param clazz     - entity class
     * @return field type
     */
    public static Class<?> getFieldType(String fieldName, Class<?> clazz) {
        String[] fieldLevels = splitByPointSeparator(fieldName);
        return getTargetClazz(fieldLevels, clazz);
    }

    private static Class<?> getTargetClazz(String[] fieldLevels, Class<?> clazz) {
        Class<?> currentClazz = clazz;
        for (int i = 0; i < fieldLevels.length; i++) {
            currentClazz = getInternalFieldType(fieldLevels[i], currentClazz);
        }
        return currentClazz;
    }

    private static Class<?> getInternalFieldType(String fieldName, Class<?> clazz) {
        Class<?> result = fieldClassMap.get(fieldName);
        if (result == null) {
            doWithFields(clazz,
                    field -> fieldClassMap.putIfAbsent(fieldName, field.getType()),
                    field -> fieldName.equals(field.getName()));
            return fieldClassMap.get(fieldName);
        }
        return result;
    }
}
