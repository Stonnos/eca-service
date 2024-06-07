package com.ecaservice.data.storage.service;

import com.ecaservice.common.web.exception.FileProcessingException;
import com.ecaservice.common.web.exception.InvalidFileException;
import com.ecaservice.data.storage.config.EcaDsConfig;
import com.ecaservice.data.storage.model.MultipartFileResource;
import eca.data.file.FileDataLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link InstancesLoader} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({InstancesLoader.class, EcaDsConfig.class})
class InstancesLoaderTest {

    @MockBean
    private FileDataLoader fileDataLoader;

    @Autowired
    private InstancesLoader instancesLoader;


    @Test
    void testLoadInstancesFromInvalidFile() {
        var multipartFileResource = mock(MultipartFileResource.class);
        when(multipartFileResource.getFile()).thenReturn("iris.sql");
        assertThrows(InvalidFileException.class, () -> instancesLoader.load(multipartFileResource));
    }

    @Test
    void testLoadInstancesWithError() throws Exception {
        var multipartFileResource = mock(MultipartFileResource.class);
        when(multipartFileResource.getFile()).thenReturn("iris.csv");
        when(fileDataLoader.loadInstances()).thenThrow(new IOException());
        assertThrows(FileProcessingException.class, () -> instancesLoader.load(multipartFileResource));
    }
}
