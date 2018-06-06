package com.ecaservice.mapping;

import com.ecaservice.model.options.AdaBoostOptions;
import com.ecaservice.model.options.HeterogeneousClassifierOptions;
import eca.ensemble.AdaBoostClassifier;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.sampling.SamplingMethod;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link HeterogeneousClassifierMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class HeterogeneousClassifierMapperTest {

    @Inject
    private HeterogeneousClassifierMapper heterogeneousClassifierMapper;

    @Test
    public void testMapHec() {
        HeterogeneousClassifier heterogeneousClassifier = new HeterogeneousClassifier();
        heterogeneousClassifier.setSamplingMethod(SamplingMethod.BAGGING);
        heterogeneousClassifier.setUseRandomClassifier(true);
        heterogeneousClassifier.setUseWeightedVotes(true);
        HeterogeneousClassifierOptions options = heterogeneousClassifierMapper.map(heterogeneousClassifier);
        Assertions.assertThat(options.getSeed()).isEqualTo(heterogeneousClassifier.getSeed());
        Assertions.assertThat(options.getNumThreads()).isEqualTo(heterogeneousClassifier.getNumThreads());
        Assertions.assertThat(options.getNumIterations()).isEqualTo(heterogeneousClassifier.getNumIterations());
        Assertions.assertThat(options.getMinError()).isEqualTo(heterogeneousClassifier.getMinError());
        Assertions.assertThat(options.getMaxError()).isEqualTo(heterogeneousClassifier.getMaxError());
        Assertions.assertThat(options.getSamplingMethod()).isEqualTo(heterogeneousClassifier.getSamplingMethod());
        Assertions.assertThat(options.getUseRandomClassifier()).isEqualTo(
                heterogeneousClassifier.getUseRandomClassifier());
        Assertions.assertThat(options.getUseWeightedVotes()).isEqualTo(heterogeneousClassifier.getUseWeightedVotes());
    }
}
