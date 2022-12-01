package com.ecaservice.server.service.experiment;

import com.ecaservice.common.web.exception.InvalidFileException;
import eca.data.file.resource.FileResource;
import org.junit.jupiter.api.Test;

import java.io.File;

import static com.ecaservice.server.TestHelperUtils.DATA_RESOURCE_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link DataService} class.
 *
 * @author Roman Batygin
 */
class DataServiceTest {

    private static final int EXPECTED_NUM_INSTANCES = 150;
    private static final int EXPECTED_NUM_ATTRIBUTES = 5;

    private DataService dataService = new DataService();

    @Test
    void testLoadInstancesFromInvalidFile() {
        FileResource fileResource = new FileResource(new File("data.png"));
        assertThrows(InvalidFileException.class, () -> dataService.load(fileResource));
    }

    @Test
    void testLoadInstancesFromFile() {
        var classLoader = ClassLoader.getSystemClassLoader();
        var fileResource = new FileResource(new File(classLoader.getResource(DATA_RESOURCE_PATH).getFile()));
        var instances = dataService.load(fileResource);
        assertThat(instances).isNotNull();
        assertThat(instances.numAttributes()).isEqualTo(EXPECTED_NUM_ATTRIBUTES);
        assertThat(instances.numInstances()).isEqualTo(EXPECTED_NUM_INSTANCES);
    }
}
