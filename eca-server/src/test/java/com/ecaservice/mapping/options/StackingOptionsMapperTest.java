package com.ecaservice.mapping.options;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.options.StackingOptions;
import eca.ensemble.StackingClassifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link StackingOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(StackingOptionsMapperImpl.class)
public class StackingOptionsMapperTest {

    @Inject
    private StackingOptionsMapper stackingOptionsMapper;

    @Test
    void testMapStackingOptions() {
        StackingOptions stackingOptions = TestHelperUtils.createStackingOptions();
        StackingClassifier stackingClassifier = stackingOptionsMapper.map(stackingOptions);
        Assertions.assertThat(stackingClassifier.getUseCrossValidation()).isEqualTo(
                stackingOptions.getUseCrossValidation());
        Assertions.assertThat(stackingClassifier.getNumFolds()).isEqualTo(stackingOptions.getNumFolds());
        Assertions.assertThat(stackingClassifier.getSeed()).isEqualTo(stackingOptions.getSeed());
    }
}
