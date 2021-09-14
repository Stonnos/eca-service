package com.ecaservice.data.storage.service;

import com.ecaservice.common.web.exception.FileProcessingException;
import com.ecaservice.common.web.exception.InvalidFileException;
import com.ecaservice.data.storage.model.MultipartFileResource;
import eca.data.file.FileDataLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link InstancesLoader} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class InstancesLoaderTest {

    @Mock
    private FileDataLoader fileDataLoader;

    @InjectMocks
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
