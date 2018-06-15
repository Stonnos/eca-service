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
 * Unit tests for checking {@link StackingClassifierMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StackingClassifierMapperTest {

    @Inject
    private StackingClassifierMapper stackingClassifierMapper;

    @Test
    public void testMapStackingClassifier() {
        StackingClassifier stackingClassifier = TestHelperUtils.createStackingClassifier();
        StackingOptions options = stackingClassifierMapper.map(stackingClassifier);
        Assertions.assertThat(options.getNumFolds()).isEqualTo(stackingClassifier.getNumFolds());
        Assertions.assertThat(options.getSeed()).isEqualTo(stackingClassifier.getSeed());
        Assertions.assertThat(options.getUseCrossValidation()).isEqualTo(stackingClassifier.getUseCrossValidation());
    }
}
