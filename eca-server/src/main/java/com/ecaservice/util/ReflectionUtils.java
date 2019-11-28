package com.ecaservice.util;

import lombok.experimental.UtilityClass;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import static com.ecaservice.util.Utils.splitByPointSeparator;

/**
 * Reflection utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ReflectionUtils {

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
        try {
            String[] fieldLevels = splitByPointSeparator(fieldName);
            return getTargetClazz(fieldLevels, clazz);
        } catch (Exception ex) {
            throw new IllegalStateException(
                    String.format("Can't found getter for field [%s] of class %s", fieldName, clazz.getName()));
        }
    }

    private static Class<?> getTargetClazz(String[] fieldLevels, Class<?> clazz)
            throws IntrospectionException, NoSuchMethodException {
        Class<?> currentClazz = clazz;
        for (int i = 0; i < fieldLevels.length; i++) {
            String getter = getGetterForField(fieldLevels[i], currentClazz);
            currentClazz = currentClazz.getMethod(getter).getReturnType();
        }
        return currentClazz;
    }

    private static String getGetterForField(String fieldName, Class<?> clazz) throws IntrospectionException {
        PropertyDescriptor propertyDescriptor = new PropertyDescriptor(fieldName, clazz);
        return propertyDescriptor.getReadMethod().getName();
    }
}
