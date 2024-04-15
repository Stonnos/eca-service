package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.TestHelperUtils;
import com.ecaservice.classifier.options.model.ExtraTreesOptions;
import eca.ensemble.forests.DecisionTreeType;
import eca.ensemble.forests.ExtraTreesClassifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Unit tests for checking {@link ExtraTreesMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(ExtraTreesMapperImpl.class)
class ExtraTreesMapperTest {

    @Autowired
    private ExtraTreesMapper extraTreesMapper;

    @Test
    void testMapExtraTrees() {
        ExtraTreesClassifier extraTreesClassifier = TestHelperUtils.createExtraTreesClassifier(DecisionTreeType.CART);
        ExtraTreesOptions options = extraTreesMapper.map(extraTreesClassifier);
        Assertions.assertThat(options.getSeed()).isEqualTo(extraTreesClassifier.getSeed());
        Assertions.assertThat(options.getNumThreads()).isEqualTo(extraTreesClassifier.getNumThreads());
        Assertions.assertThat(options.getNumIterations()).isEqualTo(extraTreesClassifier.getNumIterations());
        Assertions.assertThat(options.getMaxDepth()).isEqualTo(extraTreesClassifier.getMaxDepth());
        Assertions.assertThat(options.getMinObj()).isEqualTo(extraTreesClassifier.getMinObj());
        Assertions.assertThat(options.getNumRandomAttr()).isEqualTo(extraTreesClassifier.getNumRandomAttr());
        Assertions.assertThat(options.getDecisionTreeType()).isEqualTo(extraTreesClassifier.getDecisionTreeType());
        Assertions.assertThat(options.getNumRandomSplits()).isEqualTo(extraTreesClassifier.getNumRandomSplits());
        Assertions.assertThat(options.getUseBootstrapSamples()).isEqualTo(extraTreesClassifier.isUseBootstrapSamples());
    }
}
