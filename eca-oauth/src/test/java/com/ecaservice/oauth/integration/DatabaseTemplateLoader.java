package com.ecaservice.oauth.integration;

import freemarker.cache.TemplateLoader;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

/**
 * Implements template loading from database.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseTemplateLoader implements TemplateLoader {

    @Override
    public Object findTemplateSource(String code) throws IOException {
        log.debug("Load template [{}] from database", code);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        @Cleanup InputStream inputStream = resolver.getResource(code).getInputStream();
        @Cleanup Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return FileCopyUtils.copyToString(reader);
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
