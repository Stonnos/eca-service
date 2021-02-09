package com.ecaservice.mail.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.mail.model.TemplateEntity;
import com.ecaservice.mail.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Template management service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;

    /**
     * Gets template by code.
     *
     * @param code - template code
     * @return template entity
     */
    public TemplateEntity getTemplate(String code) {
        log.debug("Gets template by code [{}]", code);
        return templateRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException(TemplateEntity.class, code));
    }
}
