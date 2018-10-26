package com.ecaservice.mapping.options;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.options.ExtraTreesOptions;
import eca.ensemble.forests.DecisionTreeType;
import eca.ensemble.forests.ExtraTreesClassifier;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link ExtraTreesMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ExtraTreesMapperTest {

    @Inject
    private ExtraTreesMapper extraTreesMapper;

    @Test
    public void testMapExtraTrees() {
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
