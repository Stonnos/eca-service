package com.ecaservice.mapping.options;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.options.HeterogeneousClassifierOptions;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.ModifiedHeterogeneousClassifier;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link HeterogeneousClassifierOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class HeterogeneousClassifierOptionsMapperTest {

    @Inject
    private HeterogeneousClassifierOptionsMapper heterogeneousClassifierOptionsMapper;

    @Test
    public void testMapHecOptions() {
        HeterogeneousClassifierOptions options = TestHelperUtils.createHeterogeneousClassifierOptions(false);
        HeterogeneousClassifier classifier = heterogeneousClassifierOptionsMapper.map(options);
        Assertions.assertThat(classifier.getSeed()).isEqualTo(options.getSeed());
        Assertions.assertThat(classifier.getNumIterations()).isEqualTo(options.getNumIterations());
        Assertions.assertThat(classifier.getNumThreads()).isEqualTo(options.getNumThreads());
        Assertions.assertThat(classifier.getMinError()).isEqualTo(options.getMinError());
        Assertions.assertThat(classifier.getMaxError()).isEqualTo(options.getMaxError());
        Assertions.assertThat(classifier.getSamplingMethod()).isEqualTo(options.getSamplingMethod());
        Assertions.assertThat(classifier.getUseRandomClassifier()).isEqualTo(options.getUseRandomClassifier());
        Assertions.assertThat(classifier.getUseWeightedVotes()).isEqualTo(options.getUseWeightedVotes());
    }

    @Test
    public void testMapModifiedHecOptions() {
        HeterogeneousClassifierOptions options = TestHelperUtils.createHeterogeneousClassifierOptions(true);
        HeterogeneousClassifier classifier = heterogeneousClassifierOptionsMapper.map(options);
        Assertions.assertThat(classifier).isInstanceOf(ModifiedHeterogeneousClassifier.class);
    }
}
