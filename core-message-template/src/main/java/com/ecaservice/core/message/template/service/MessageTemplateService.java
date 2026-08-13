package com.ecaservice.core.message.template.service;

import com.ecaservice.core.message.template.config.MessageTemplateConfig;
import com.ecaservice.core.message.template.model.MessageTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Message templates service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageTemplateService {

    private final MessageTemplateConfig messageTemplateConfig;

    /**
     * Gets message template by code.
     *
     * @param code - template code
     * @return message template
     */
    public MessageTemplate getTemplate(String code) {
        log.debug("Starting to load message template [{}]", code);
        var messageTemplate = messageTemplateConfig.getTemplates()
                .stream()
                .filter(template -> template.getCode().equals(code))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException(String.format("Can't find template with code [%s]", code)));
        log.debug("Message template [{}] has been loaded", code);
        return messageTemplate;
    }
}
