package com.ecaservice.core.message.template.config;

import com.ecaservice.core.message.template.service.MessageTemplateService;
import freemarker.cache.TemplateLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Reader;
import java.io.StringReader;

/**
 * Implements message template loading from database.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageTemplateLoader implements TemplateLoader {

    private final MessageTemplateService messageTemplateService;

    @Override
    public Object findTemplateSource(String code) {
        log.debug("Load message template [{}] from database", code);
        return messageTemplateService.getTemplate(code).getTemplateText();
    }

    @Override
    public long getLastModified(Object template) {
        return -1L;
    }

    @Override
    public Reader getReader(Object template, String encoding) {
        return new StringReader(String.valueOf(template));
    }

    @Override
    public void closeTemplateSource(Object template) {
        //Empty implementation
    }
}
