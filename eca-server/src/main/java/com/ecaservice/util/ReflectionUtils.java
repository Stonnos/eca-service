package com.ecaservice.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 * Reflection utility class.
 *
 * @author Roman Batygin
 */
public class ReflectionUtils {

    private ReflectionUtils() {

    }

    /**
     * Gets getter method name by field property.
     *
     * @param fieldName - field name
     * @param clazz     - entity class
     * @return getter name
     * @throws IntrospectionException in case of errors
     */
    public static String getGetterName(String fieldName, Class<?> clazz) throws IntrospectionException {
        PropertyDescriptor propertyDescriptor = new PropertyDescriptor(fieldName, clazz);
        return propertyDescriptor.getReadMethod().getName();
    }

    /**
     * Gets getter return type by field property.
     *
     * @param fieldName - field name
     * @param clazz     - entity class
     * @return getter return type
     * @throws IntrospectionException in case on errors
     * @throws NoSuchMethodException  in case if method is not exists
     */
    public static Class<?> getGetterReturnType(String fieldName, Class<?> clazz)
            throws IntrospectionException, NoSuchMethodException {
        String getter = getGetterName(fieldName, clazz);
        return clazz.getMethod(getter).getReturnType();
    }
}
