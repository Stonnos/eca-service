package com.ecaservice.oauth.service;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Base64;

/**
 * Serialization helper.
 *
 * @author Roman Batygin
 */
@Component
public class SerializationHelper {

    /**
     * Serializes object into base64 string.
     *
     * @param object - object
     * @return serialized object base64 string
     */
    public String serialize(Serializable object) {
        byte[] bytes = SerializationUtils.serialize(object);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Deserializes base64 string to object.
     *
     * @param base64String - base64 string
     * @param <T>          - object generic type
     * @return result object
     */
    public <T> T deserialize(String base64String) {
        byte[] bytes = Base64.getDecoder().decode(base64String);
        return SerializationUtils.deserialize(bytes);
    }
}
