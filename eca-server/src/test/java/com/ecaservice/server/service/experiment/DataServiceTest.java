package com.ecaservice.server.service.experiment;

import com.ecaservice.common.web.exception.InvalidFileException;
import eca.data.file.resource.FileResource;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link DataService} class.
 *
 * @author Roman Batygin
 */
class DataServiceTest {

    private DataService dataService = new DataService();

    @Test
    void testLoadInstancesFromInvalidFile() {
        FileResource fileResource = new FileResource(new File("data.png"));
        assertThrows(InvalidFileException.class, () -> dataService.load(fileResource));
    }
}
