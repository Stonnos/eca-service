package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.TestHelperUtils;
import com.ecaservice.classifier.options.model.StackingOptions;
import eca.ensemble.StackingClassifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Unit tests for checking {@link StackingClassifierMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(StackingClassifierMapperImpl.class)
class StackingClassifierMapperTest {

    @Autowired
    private StackingClassifierMapper stackingClassifierMapper;

    @Test
    void testMapStackingClassifier() {
        StackingClassifier stackingClassifier = TestHelperUtils.createStackingClassifier();
        StackingOptions options = stackingClassifierMapper.map(stackingClassifier);
        Assertions.assertThat(options.getNumFolds()).isEqualTo(stackingClassifier.getNumFolds());
        Assertions.assertThat(options.getSeed()).isEqualTo(stackingClassifier.getSeed());
        Assertions.assertThat(options.getUseCrossValidation()).isEqualTo(stackingClassifier.getUseCrossValidation());
    }
}
