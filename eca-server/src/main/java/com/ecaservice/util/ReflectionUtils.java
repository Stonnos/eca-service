package com.ecaservice.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import static com.ecaservice.util.Utils.splitByPointSeparator;

/**
 * Reflection utility class.
 *
 * @author Roman Batygin
 */
public class ReflectionUtils {

    private ReflectionUtils() {

    }

    /**
     * Gets getter method return type by field property. Note: Property can be composite, for example:
     * <pre>
     *  entity.value1
     * </pre>
     * If the field is composite, then the last property value is taken. In our case the last value is value1.
     *
     * @param fieldName - field name
     * @param clazz     - entity class
     * @return getter name
     * @throws IntrospectionException in case of errors
     * @throws NoSuchMethodException  in case if method is not exists
     */
    public static Class<?> getGetterReturnType(String fieldName, Class<?> clazz)
            throws IntrospectionException, NoSuchMethodException {
        String[] fieldLevels = splitByPointSeparator(fieldName);
        String targetField = getTargetField(fieldLevels);
        Class<?> targetClazz = getTargetClazz(fieldLevels, clazz);
        String getter = getGetterForField(targetField, targetClazz);
        return targetClazz.getMethod(getter).getReturnType();
    }

    private static Class<?> getTargetClazz(String[] fieldLevels, Class<?> clazz)
            throws IntrospectionException, NoSuchMethodException {
        return fieldLevels.length > 1 ? getGetterReturnType(fieldLevels[0], clazz) : clazz;
    }

    private static String getTargetField(String[] fieldLevels) {
        return fieldLevels.length > 1 ? fieldLevels[1] : fieldLevels[0];
    }

    private static String getGetterForField(String fieldName, Class<?> clazz) throws IntrospectionException {
        PropertyDescriptor propertyDescriptor = new PropertyDescriptor(fieldName, clazz);
        return propertyDescriptor.getReadMethod().getName();
    }
}
