package com.ecaservice.common.web.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link JsonResourceLoader} class.
 *
 * @author Roman Batygin
 */
class JsonResourceLoaderTest {

    private static final String VALID_CONFIG_PATH = "valid-config.json";
    private static final String VALID_CONFIG_LIST_PATH = "valid-config-list.json";
    private static final String NON_EXISTENT_FILE_PATH = "not-found.json";
    private static final String VALID_LOCATION_PATTERN = "classpath*:json-loader/*.json";

    private static final String CONFIG_NAME = "test-config";
    private static final int CONFIG_VALUE = 42;
    private static final String FIRST_CONFIG_NAME = "first-config";
    private static final int FIRST_CONFIG_VALUE = 1;
    private static final String SECOND_CONFIG_NAME = "second-config";
    private static final int SECOND_CONFIG_VALUE = 2;

    private JsonResourceLoader jsonResourceLoader;

    @BeforeEach
    void setUp() {
        jsonResourceLoader = new JsonResourceLoader();
    }

    @Test
    void testLoadByClassSuccess() throws IOException {
        TestConfig result = jsonResourceLoader.load(VALID_CONFIG_PATH, TestConfig.class);
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(CONFIG_NAME);
        assertThat(result.getValue()).isEqualTo(CONFIG_VALUE);
    }

    @Test
    void testLoadByClassWhenFileNotFound() {
        assertThatThrownBy(() -> jsonResourceLoader.load(NON_EXISTENT_FILE_PATH, TestConfig.class))
                .isInstanceOf(FileNotFoundException.class);
    }

    @Test
    void testLoadAllSuccess() {
        List<TestConfig> result = jsonResourceLoader.loadAll(VALID_LOCATION_PATTERN, TestConfig.class);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo(CONFIG_NAME);
        assertThat(result.get(0).getValue()).isEqualTo(CONFIG_VALUE);
    }

    @Test
    void testLoadByTypeReferenceSuccess() throws IOException {
        List<TestConfig> result = jsonResourceLoader.load(
                VALID_CONFIG_LIST_PATH,
                new TypeReference<>() {
                }
        );
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo(FIRST_CONFIG_NAME);
        assertThat(result.get(0).getValue()).isEqualTo(FIRST_CONFIG_VALUE);
        assertThat(result.get(1).getName()).isEqualTo(SECOND_CONFIG_NAME);
        assertThat(result.get(1).getValue()).isEqualTo(SECOND_CONFIG_VALUE);
    }

    @Test
    void testLoadByTypeReferenceWhenFileNotFound() {
        TypeReference<List<TestConfig>> typeRef = new TypeReference<>() {
        };
        assertThatThrownBy(() -> jsonResourceLoader.load(NON_EXISTENT_FILE_PATH, typeRef))
                .isInstanceOf(FileNotFoundException.class);
    }

    @Data
    private static class TestConfig {
        private String name;
        private int value;
    }
}
