package com.ecaservice.core.redelivery.converter.impl;

import com.ecaservice.core.redelivery.converter.RequestMessageConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Json message converter.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
public class JsonRequestMessageConverter implements RequestMessageConverter {

    private ObjectMapper objectMapper;

    @Override
    public <T> String convert(T obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    @Override
    public <T> T convert(String obj, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(obj, clazz);
    }
}
