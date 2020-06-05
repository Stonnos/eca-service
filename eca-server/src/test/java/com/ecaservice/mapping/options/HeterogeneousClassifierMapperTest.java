package com.ecaservice.mapping.options;

import com.ecaservice.model.options.HeterogeneousClassifierOptions;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.ModifiedHeterogeneousClassifier;
import eca.ensemble.sampling.SamplingMethod;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link HeterogeneousClassifierMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(HeterogeneousClassifierMapperImpl.class)
class HeterogeneousClassifierMapperTest {

    @Inject
    private HeterogeneousClassifierMapper heterogeneousClassifierMapper;

    @Test
    void testMapHec() {
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
        Assertions.assertThat(options.getUseRandomSubspaces()).isFalse();
    }

    @Test
    void testMapModifiedHec() {
        HeterogeneousClassifierOptions options =
                heterogeneousClassifierMapper.map(new ModifiedHeterogeneousClassifier());
        Assertions.assertThat(options.getUseRandomSubspaces()).isTrue();
    }
}
