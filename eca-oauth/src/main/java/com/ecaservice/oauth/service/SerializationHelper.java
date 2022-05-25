package com.ecaservice.oauth.service;

import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.stereotype.Component;

/**
 * Serialization helper.
 *
 * @author Roman Batygin
 */
@Component
public class SerializationHelper {

    /**
     * Serializes object into bytes array.
     *
     * @param object - object
     * @return serialized object as bytes array
     */
    public byte[] serialize(Object object) {
        return SerializationUtils.serialize(object);
    }

    /**
     * Deserializes object to byte array.
     *
     * @param bytes - bytes
     * @param <T>   - object generic type
     * @return result object
     */
    public <T> T deserialize(byte[] bytes) {
        return SerializationUtils.deserialize(bytes);
    }
}
