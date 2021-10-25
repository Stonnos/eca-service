package com.ecaservice.core.mail.client.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.util.Map;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Reads variables map from json.
     *
     * @param variablesJson - variables json
     * @return variables map
     */
    public static Map<String, String> readVariables(String variablesJson) {
        try {
            return OBJECT_MAPPER.readValue(variablesJson, new TypeReference<>() {
            });
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }

    /**
     * Converts variables map to json.
     *
     * @param variables - variables map
     * @return variables map json string
     */
    public static String toJson(Map<String, String> variables) {
        try {
            return OBJECT_MAPPER.writeValueAsString(variables);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }
}
