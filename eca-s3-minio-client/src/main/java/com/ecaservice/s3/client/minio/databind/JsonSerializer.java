package com.ecaservice.s3.client.minio.databind;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Json serializer.
 *
 * @author Roman Batygin
 */
public class JsonSerializer implements ObjectSerializer {

    private final ObjectMapper objectMapper =  new ObjectMapper();

    @Override
    public byte[] serialize(Object object) throws IOException {
       return objectMapper.writeValueAsBytes(object);
    }
}
