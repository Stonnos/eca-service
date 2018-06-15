package com.ecaservice.mapping.options;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.options.StackingOptions;
import eca.ensemble.StackingClassifier;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link StackingOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StackingOptionsMapperTest {

    @Inject
    private StackingOptionsMapper stackingOptionsMapper;

    @Test
    public void testMapStackingOptions() {
        StackingOptions stackingOptions = TestHelperUtils.createStackingOptions();
        StackingClassifier stackingClassifier = stackingOptionsMapper.map(stackingOptions);
        Assertions.assertThat(stackingClassifier.getUseCrossValidation()).isEqualTo(
                stackingOptions.getUseCrossValidation());
        Assertions.assertThat(stackingClassifier.getNumFolds()).isEqualTo(stackingOptions.getNumFolds());
        Assertions.assertThat(stackingClassifier.getSeed()).isEqualTo(stackingOptions.getSeed());
    }
}
