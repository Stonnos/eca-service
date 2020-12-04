package com.ecaservice.external.api.validation;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static com.ecaservice.external.api.TestHelperUtils.createInstancesMockMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link TrainDataFileValidator} functionality.
 *
 * @author Roman Batygin
 */
class TrainDataFileValidatorTest {

    private static final String DATA_CSV = "data.csv";
    private static final String DATA_ISO = "data.iso";

    private TrainDataFileValidator trainDataFileValidator = new TrainDataFileValidator();

    @Test
    void testInvalidTrainDataExtension() {
        MockMultipartFile file = createInstancesMockMultipartFile(DATA_ISO);
        assertThat(trainDataFileValidator.isValid(file, null)).isFalse();
    }

    @Test
    void testValidTrainDataExtension() {
        MockMultipartFile file = createInstancesMockMultipartFile(DATA_CSV);
        assertThat(trainDataFileValidator.isValid(file, null)).isTrue();
    }
}
