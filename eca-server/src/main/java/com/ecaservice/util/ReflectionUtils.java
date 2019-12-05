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
     * Gets getter method return type by field property. Note: Property can be composite, for example:
     * <pre>
     *  entity.entity1.prop1
     * </pre>
     * If the field is composite, then the last property name is taken. In our case the last name is prop1.
     *
     * @param fieldName - field name
     * @param clazz     - entity class
     * @return getter return type
     */
    public static Class<?> getGetterReturnType(String fieldName, Class<?> clazz) {
        String[] fieldLevels = splitByPointSeparator(fieldName);
        return getTargetClazz(fieldLevels, clazz);
    }

    private static Class<?> getTargetClazz(String[] fieldLevels, Class<?> clazz) {
        Class<?> currentClazz = clazz;
        for (int i = 0; i < fieldLevels.length; i++) {
            currentClazz = getFieldReturnType(fieldLevels[i], currentClazz);
        }
        return currentClazz;
    }

    private static Class<?> getFieldReturnType(String fieldName, Class<?> clazz) {
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
