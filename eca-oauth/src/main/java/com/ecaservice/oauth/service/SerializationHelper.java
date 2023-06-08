package com.ecaservice.oauth.service;

import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

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
    public String serialize(Object object) {
        byte[] bytes = SerializationUtils.serialize(object);
        return Base64Utils.encodeToString(bytes);
    }

    /**
     * Deserializes base64 string to object.
     *
     * @param base64String - base64 string
     * @param <T>          - object generic type
     * @return result object
     */
    public <T> T deserialize(String base64String) {
        byte[] bytes = Base64Utils.decodeFromString(base64String);
        return SerializationUtils.deserialize(bytes);
    }
}
