package com.ecaservice.s3.client.minio.databind;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Json deserializer.
 *
 * @param <T> object generic type
 * @author Roman Batygin
 */
public class JsonDeserializer<T> implements ObjectDeserializer<T> {

    private final ObjectMapper objectMapper =  new ObjectMapper();

    @Override
    public T deserialize(InputStream inputStream, Class<T> targetClazz) throws IOException {
        return objectMapper.readValue(inputStream, targetClazz);
    }
}
