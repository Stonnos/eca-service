package com.ecaservice.mapping.options;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.options.DecisionTreeOptions;
import com.ecaservice.model.options.OptionsVariables;
import eca.ensemble.forests.DecisionTreeType;
import eca.trees.CHAID;
import eca.trees.DecisionTreeClassifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link DecisionTreeMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(DecisionTreeMapperImpl.class)
public class DecisionTreeMapperTest {

    @Inject
    private DecisionTreeMapper decisionTreeMapper;

    @Test
    void testMapDecisionTree() {
        DecisionTreeClassifier treeClassifier = TestHelperUtils.createDecisionTreeClassifier(DecisionTreeType.C45);
        DecisionTreeOptions options = decisionTreeMapper.map(treeClassifier);
        Assertions.assertThat(options.getSeed()).isEqualTo(treeClassifier.getSeed());
        Assertions.assertThat(options.getNumRandomSplits()).isEqualTo(treeClassifier.getNumRandomSplits());
        Assertions.assertThat(options.getMinObj()).isEqualTo(treeClassifier.getMinObj());
        Assertions.assertThat(options.getMaxDepth()).isEqualTo(treeClassifier.getMaxDepth());
        Assertions.assertThat(options.getNumRandomAttr()).isEqualTo(treeClassifier.getNumRandomAttr());
        Assertions.assertThat(options.getRandomTree()).isEqualTo(treeClassifier.isRandomTree());
        Assertions.assertThat(options.getUseBinarySplits()).isEqualTo(treeClassifier.getUseBinarySplits());
        Assertions.assertThat(options.getUseRandomSplits()).isEqualTo(treeClassifier.isUseRandomSplits());
        Assertions.assertThat(options.getDecisionTreeType()).isEqualTo(DecisionTreeType.C45);
    }

    @Test
    void testMapChaid() {
        DecisionTreeClassifier treeClassifier = TestHelperUtils.createDecisionTreeClassifier(DecisionTreeType.CHAID);
        DecisionTreeOptions options = decisionTreeMapper.map(treeClassifier);
        Assertions.assertThat(options.getDecisionTreeType()).isEqualTo(DecisionTreeType.CHAID);
        Assertions.assertThat(options.getAdditionalOptions()).isNotEmpty();
        Assertions.assertThat(Double.valueOf(options.getAdditionalOptions().get(OptionsVariables.ALPHA))).isEqualTo(
                ((CHAID) treeClassifier).getAlpha());
    }
}
