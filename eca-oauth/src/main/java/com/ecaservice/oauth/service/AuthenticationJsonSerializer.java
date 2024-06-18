package com.ecaservice.oauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.Authentication;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.stereotype.Component;

/**
 * Authentication json serializer.
 *
 * @author Roman Batygin
 */
@Component
public class AuthenticationJsonSerializer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Init method.
     */
    @PostConstruct
    public void init() {
        objectMapper.registerModule(new CoreJackson2Module());
    }

    /**
     * Serializes authentication into json string.
     *
     * @param authentication - object
     * @return serialized authentication json string
     */
    public String serialize(Authentication authentication) {
        try {
            return objectMapper.writeValueAsString(authentication);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Deserializes authentication json string to object.
     *
     * @param jsonValue - authentication json string
     * @return authentication object
     */
    public Authentication deserialize(String jsonValue) {
        try {
            return objectMapper.readValue(jsonValue, Authentication.class);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
