package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.AdaBoostOptions;
import eca.ensemble.AdaBoostClassifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link AdaBoostMapperTest} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(AdaBoostMapperImpl.class)
class AdaBoostMapperTest {

    @Inject
    private AdaBoostMapper adaBoostMapper;

    @Test
    void testMapAdaBoost() {
        AdaBoostClassifier adaBoostClassifier = new AdaBoostClassifier();
        AdaBoostOptions options = adaBoostMapper.map(adaBoostClassifier);
        Assertions.assertThat(options.getSeed()).isEqualTo(adaBoostClassifier.getSeed());
        Assertions.assertThat(options.getNumThreads()).isEqualTo(adaBoostClassifier.getNumThreads());
        Assertions.assertThat(options.getNumIterations()).isEqualTo(adaBoostClassifier.getNumIterations());
        Assertions.assertThat(options.getMinError()).isEqualTo(adaBoostClassifier.getMinError());
        Assertions.assertThat(options.getMaxError()).isEqualTo(adaBoostClassifier.getMaxError());
    }
}
