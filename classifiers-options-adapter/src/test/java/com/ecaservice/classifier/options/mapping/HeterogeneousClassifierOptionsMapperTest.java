package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.TestHelperUtils;
import com.ecaservice.classifier.options.model.HeterogeneousClassifierOptions;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.ModifiedHeterogeneousClassifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Unit tests for checking {@link HeterogeneousClassifierOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({HeterogeneousClassifierOptionsMapperImpl.class, HeterogeneousClassifierFactory.class})
class HeterogeneousClassifierOptionsMapperTest {

    @Autowired
    private HeterogeneousClassifierOptionsMapper heterogeneousClassifierOptionsMapper;

    @Test
    void testMapHecOptions() {
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
    void testMapModifiedHecOptions() {
        HeterogeneousClassifierOptions options = TestHelperUtils.createHeterogeneousClassifierOptions(true);
        HeterogeneousClassifier classifier = heterogeneousClassifierOptionsMapper.map(options);
        Assertions.assertThat(classifier).isInstanceOf(ModifiedHeterogeneousClassifier.class);
    }
}
