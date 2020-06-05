package com.ecaservice.mapping.options;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.options.AdaBoostOptions;
import eca.ensemble.AdaBoostClassifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link AdaBoostOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(AdaBoostOptionsMapperImpl.class)
class AdaBoostOptionsMapperTest {

    @Inject
    private AdaBoostOptionsMapper adaBoostOptionsMapper;

    @Test
    void testMapAdaBoostOptions() {
        AdaBoostOptions adaBoostOptions = TestHelperUtils.createAdaBoostOptions();
        AdaBoostClassifier classifier = adaBoostOptionsMapper.map(adaBoostOptions);
        Assertions.assertThat(classifier.getSeed()).isEqualTo(adaBoostOptions.getSeed());
        Assertions.assertThat(classifier.getNumIterations()).isEqualTo(adaBoostOptions.getNumIterations());
        Assertions.assertThat(classifier.getNumThreads()).isEqualTo(adaBoostOptions.getNumThreads());
        Assertions.assertThat(classifier.getMinError()).isEqualTo(adaBoostOptions.getMinError());
        Assertions.assertThat(classifier.getMaxError()).isEqualTo(adaBoostOptions.getMaxError());

    }
}
