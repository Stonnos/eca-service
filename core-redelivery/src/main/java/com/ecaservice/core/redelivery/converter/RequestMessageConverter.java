package com.ecaservice.core.redelivery.converter;

/**
 * Request message converter interface.
 *
 * @author Roman Batygin
 */
public interface RequestMessageConverter {

    /**
     * Serializes java object to string.
     *
     * @param obj - java object
     * @param <T> - java object generic type
     * @return serialized java object
     * @throws Exception in case of error
     */
    <T> String convert(T obj) throws Exception;

    /**
     * Deserializes java object from string.
     *
     * @param obj   - java object string
     * @param clazz - java object class
     * @param <T>   - java object generic type
     * @return java object
     * @throws Exception in case of error
     */
    <T> T convert(String obj, Class<T> clazz) throws Exception;
}
