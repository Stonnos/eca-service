package com.ecaservice.common.web.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Load json from specified resource file.
     *
     * @param file  - resource json file
     * @param clazz - object type class
     * @return result object
     */
    public <T> T load(String file, Class<T> clazz) throws IOException {
        log.info("Starting to load json config from file [{}]", file);
        var resource = resolver.getResource(file);
        @Cleanup var inputStream = resource.getInputStream();
        T value = objectMapper.readValue(inputStream, clazz);
        log.info("Json config has been loaded from file [{}]", file);
        return value;
    }

    /**
     * Load json configs from specified resource location.
     *
     * @param location - resource location
     * @param clazz    - object type class
     * @return result list
     */
    public <T> List<T> loadAll(String location, Class<T> clazz) {
        Resource[] resources = getResources(location);
        List<T> values = new ArrayList<>();
        if (resources.length == 0) {
            log.info("No one config [{}] type has been found. Skipped...", clazz.getSimpleName());
        } else {
            log.info("Starting to load [{}] configs [{}] type", resources.length, clazz.getSimpleName());
            for (Resource resource : resources) {
                String fileName = resource.getFilename();
                try {
                    @Cleanup var inputStream = resource.getInputStream();
                    var template = objectMapper.readValue(inputStream, clazz);
                    values.add(template);
                } catch (IOException ex) {
                    String errorMessage =
                            String.format("There was an error while read config [%s] type from file [%s]: %s",
                                    clazz.getSimpleName(), fileName, ex.getMessage());
                    throw new IllegalStateException(errorMessage);
                }
            }
            log.info("[{}] configs of type [{}] has been loaded", resources.length, clazz.getSimpleName());
        }
        return values;
    }

    @SneakyThrows
    private Resource[] getResources(String location) {
        return resolver.getResources(location);
    }
}
