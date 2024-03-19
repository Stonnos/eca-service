package com.ecaservice.data.loader.validation;

import com.ecaservice.data.loader.config.AppProperties;
import com.ecaservice.data.loader.exception.InvalidTrainDataFormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.io.IOException;

import static com.ecaservice.data.loader.TestHelperUtils.loadInstances;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link InstancesValidationService} class.
 *
 * @author Roman Batygin
 */
@ComponentScan(basePackageClasses = InstancesValidationService.class)
@Import(AppProperties.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@ExtendWith(SpringExtension.class)
class InstancesValidationServiceTest {

    @Inject
    private InstancesValidationService instancesValidationService;

    @Test
    void testValidInstances() throws IOException {
        var instancesModel = loadInstances();
        instancesValidationService.validate(instancesModel);
    }

    @ParameterizedTest
    @ArgumentsSource(InstancesTestDataProvider.class)
    void testValidationError(String instancesLocation) throws IOException {
        var instancesModel = loadInstances(instancesLocation);
        assertThrows(InvalidTrainDataFormatException.class, () -> instancesValidationService.validate(instancesModel));
    }
}
