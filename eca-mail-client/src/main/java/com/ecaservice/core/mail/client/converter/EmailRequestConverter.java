package com.ecaservice.core.mail.client.converter;

import com.ecaservice.common.web.crypto.EncryptorBase64AdapterService;
import com.ecaservice.core.mail.client.config.EcaMailClientProperties;
import com.ecaservice.core.redelivery.converter.RequestMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailRequestConverter implements RequestMessageConverter {

    private static final String CIPHER_PREFIX = "{cipher}";

    private final EcaMailClientProperties ecaMailClientProperties;
    private final EncryptorBase64AdapterService encryptorBase64AdapterService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> String convert(T message) throws Exception {
        String json = objectMapper.writeValueAsString(message);
        if (Boolean.TRUE.equals(ecaMailClientProperties.getEncrypt().getEnabled())) {
            return String.format("%s%s", CIPHER_PREFIX, encryptorBase64AdapterService.encrypt(json));
        }
        return json;
    }

    @Override
    public <T> T convert(String message, Class<T> clazz) throws Exception {
        String json = decrypt(message);
        return objectMapper.readValue(json, clazz);
    }

    private String decrypt(String message) {
        if (StringUtils.startsWith(message, CIPHER_PREFIX)) {
            String cipher = StringUtils.substringAfter(message, CIPHER_PREFIX);
            return encryptorBase64AdapterService.decrypt(cipher);
        }
        return message;
    }
}
