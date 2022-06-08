package com.ecaservice.config.swagger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Open api custom configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class OpenApi30CustomConfiguration {

    private final OpenApiProperties openApiProperties;
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Creates open api customizer bean.
     *
     * @return open api customizer bean
     */
    @Bean
    public OpenApiCustomiser openApiCustomiser() {
        return openApi -> {
            log.info("Starting to customize Open API");
            loadCustomExamples(openApi);
            log.info("Open API customization has been finished");
        };
    }

    private void loadCustomExamples(OpenAPI openAPI) {
        if (StringUtils.isEmpty(openApiProperties.getExamplesLocation())) {
            log.warn("Open API examples location not specified. Skipped...");
        } else {
            try {
                Resource[] resources = resolver.getResources(openApiProperties.getExamplesLocation());
                if (resources.length == 0) {
                    log.info("No one open api example has been found. Skipped...");
                } else {
                    log.info("Starting to load [{}] Open API examples", resources.length);
                    Stream.of(resources).forEach(resource -> loadExample(openAPI, resource));
                    log.info("[{}] Open API examples has been loaded", resources.length);
                }
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }

    private void loadExample(OpenAPI openAPI, Resource resource) {
        String fileName = resource.getFilename();
        try {
            log.info("Starting to read Open API example from file [{}]", fileName);
            @Cleanup var inputStream = resource.getInputStream();
            var exampleValue = objectMapper.readValue(inputStream, new TypeReference<Map<Object, Object>>() {
            });
            String exampleKey = FilenameUtils.getBaseName(fileName);
            addExample(openAPI, exampleKey, exampleValue);
            log.info("Open API example has been read from file [{}]", fileName);
        } catch (IOException ex) {
            String errorMessage =
                    String.format("There was an error while read Open API example from file [%s]: %s", fileName,
                            ex.getMessage());
            throw new IllegalStateException(errorMessage);
        }
    }

    private void addExample(OpenAPI openAPI, String exampleKey, Object exampleValue) {
        if (CollectionUtils.isEmpty(openAPI.getComponents().getExamples())) {
            openAPI.getComponents().setExamples(newHashMap());
        }
        if (openAPI.getComponents().getExamples().containsKey(exampleKey)) {
            throw new IllegalStateException(String.format("Duplicate Open API example with key [%s]", exampleKey));
        }
        Example example = new Example();
        example.setValue(exampleValue);
        openAPI.getComponents().getExamples().put(exampleKey, example);
    }
}
