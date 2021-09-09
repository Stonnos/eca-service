package com.ecaservice.service.experiment;

import com.ecaservice.common.web.exception.InvalidFileException;
import eca.data.file.FileDataSaver;
import eca.data.file.resource.FileResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link DataService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class DataServiceTest {

    @Mock
    private FileDataSaver fileDataSaver;

    @InjectMocks
    private DataService dataService;

    @Test
    void testLoadInstancesFromInvalidFile() {
        FileResource fileResource = new FileResource(new File("data.png"));
        assertThrows(InvalidFileException.class, () -> dataService.load(fileResource));
    }
}
