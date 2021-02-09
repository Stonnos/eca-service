package com.ecaservice.mail.config.freemarker;

import com.ecaservice.mail.repository.TemplateRepository;
import freemarker.cache.TemplateLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Reader;
import java.io.StringReader;

/**
 * Implements template loading from database.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseTemplateLoader implements TemplateLoader {

    private final TemplateRepository templateRepository;

    @Override
    public Object findTemplateSource(String code) {
        log.debug("Load template [{}] from database", code);
        return templateRepository.getBodyByCode(code);
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
