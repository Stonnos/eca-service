package com.ecaservice.common.web.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

/**
 * Json resource loader.
 *
 * @author Roman Batygin
 */
@Slf4j
public class JsonResourceLoader {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    /**
     * Load json from specified resource file.
     *
     * @param file         - resource json file
     * @param valueTypeRef - object type reference
     * @return result object
     */
    public <T> T load(String file, TypeReference<T> valueTypeRef) throws IOException {
        log.info("Starting to load json config from file [{}]", file);
        var resource = resolver.getResource(file);
        @Cleanup var inputStream = resource.getInputStream();
        T value = objectMapper.readValue(inputStream, valueTypeRef);
        log.info("Json config has been loaded from file [{}]", file);
        return value;
    }
}
