package com.ecaservice.mapping.options;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.options.AdaBoostOptions;
import eca.ensemble.AdaBoostClassifier;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link AdaBoostOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(AdaBoostOptionsMapperImpl.class)
public class AdaBoostOptionsMapperTest {

    @Inject
    private AdaBoostOptionsMapper adaBoostOptionsMapper;

    @Test
    public void testMapAdaBoostOptions() {
        AdaBoostOptions adaBoostOptions = TestHelperUtils.createAdaBoostOptions();
        AdaBoostClassifier classifier = adaBoostOptionsMapper.map(adaBoostOptions);
        Assertions.assertThat(classifier.getSeed()).isEqualTo(adaBoostOptions.getSeed());
        Assertions.assertThat(classifier.getNumIterations()).isEqualTo(adaBoostOptions.getNumIterations());
        Assertions.assertThat(classifier.getNumThreads()).isEqualTo(adaBoostOptions.getNumThreads());
        Assertions.assertThat(classifier.getMinError()).isEqualTo(adaBoostOptions.getMinError());
        Assertions.assertThat(classifier.getMaxError()).isEqualTo(adaBoostOptions.getMaxError());

    }
}
