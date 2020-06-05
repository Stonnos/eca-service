package com.ecaservice.mapping.options;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.options.ExtraTreesOptions;
import eca.ensemble.forests.DecisionTreeType;
import eca.ensemble.forests.ExtraTreesClassifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link ExtraTreesOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(ExtraTreesOptionsMapperImpl.class)
class ExtraTreesOptionsMapperTest {

    @Inject
    private ExtraTreesOptionsMapper extraTreesOptionsMapper;

    @Test
    void testMapExtraTreesOptions() {
        ExtraTreesOptions options = TestHelperUtils.createExtraTreesOptions(DecisionTreeType.CART);
        ExtraTreesClassifier extraTreesClassifier = extraTreesOptionsMapper.map(options);
        Assertions.assertThat(extraTreesClassifier.getSeed()).isEqualTo(options.getSeed());
        Assertions.assertThat(extraTreesClassifier.getNumIterations()).isEqualTo(options.getNumIterations());
        Assertions.assertThat(extraTreesClassifier.getNumThreads()).isEqualTo(options.getNumThreads());
        Assertions.assertThat(extraTreesClassifier.getDecisionTreeType()).isEqualTo(options.getDecisionTreeType());
        Assertions.assertThat(extraTreesClassifier.getMaxDepth()).isEqualTo(options.getMaxDepth());
        Assertions.assertThat(extraTreesClassifier.getMinObj()).isEqualTo(options.getMinObj());
        Assertions.assertThat(extraTreesClassifier.getNumRandomAttr()).isEqualTo(options.getNumRandomAttr());
        Assertions.assertThat(extraTreesClassifier.getNumRandomSplits()).isEqualTo(options.getNumRandomSplits());
        Assertions.assertThat(extraTreesClassifier.isUseBootstrapSamples()).isEqualTo(options.getUseBootstrapSamples());
    }
}
