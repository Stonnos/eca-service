package com.ecaservice.common.web.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

/**
 * Json utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Converts object to json string.
     *
     * @param object - object
     * @return json string
     */
    public static String toJson(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Converts json string to object.
     *
     * @param json  - json string
     * @param clazz - object class
     * @param <T>   - object generic type
     * @return result object
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
