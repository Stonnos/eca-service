package com.ecaservice.core.message.template.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.message.template.entity.MessageTemplateEntity;
import com.ecaservice.core.message.template.repository.MessageTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.ecaservice.core.message.template.config.cache.MessageTemplateCacheNames.MESSAGE_TEMPLATES_CACHE_NAME;

/**
 * Message templates service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageTemplateService {

    private final MessageTemplateRepository messageTemplateRepository;

    /**
     * Gets message template by id.
     *
     * @param id - template id
     * @return message template entity
     */
    @Cacheable(MESSAGE_TEMPLATES_CACHE_NAME)
    public MessageTemplateEntity getTemplate(String id) {
        log.info("Starting to load message template [{}]", id);
        var messageTemplate = messageTemplateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageTemplateEntity.class, id));
        log.info("Message template [{}] has been loaded", id);
        return messageTemplate;
    }
}
