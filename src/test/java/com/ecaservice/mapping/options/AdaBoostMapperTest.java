package com.ecaservice.mapping.options;

import com.ecaservice.model.options.AdaBoostOptions;
import eca.ensemble.AdaBoostClassifier;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link AdaBoostMapperTest} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(AdaBoostMapperImpl.class)
public class AdaBoostMapperTest {

    @Inject
    private AdaBoostMapper adaBoostMapper;

    @Test
    public void testMapAdaBoost() {
        AdaBoostClassifier adaBoostClassifier = new AdaBoostClassifier();
        AdaBoostOptions options = adaBoostMapper.map(adaBoostClassifier);
        Assertions.assertThat(options.getSeed()).isEqualTo(adaBoostClassifier.getSeed());
        Assertions.assertThat(options.getNumThreads()).isEqualTo(adaBoostClassifier.getNumThreads());
        Assertions.assertThat(options.getNumIterations()).isEqualTo(adaBoostClassifier.getNumIterations());
        Assertions.assertThat(options.getMinError()).isEqualTo(adaBoostClassifier.getMinError());
        Assertions.assertThat(options.getMaxError()).isEqualTo(adaBoostClassifier.getMaxError());
    }
}
