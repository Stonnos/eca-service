package com.ecaservice.mapping.options;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.options.DecisionTreeOptions;
import com.ecaservice.model.options.OptionsVariables;
import eca.ensemble.forests.DecisionTreeType;
import eca.trees.CHAID;
import eca.trees.DecisionTreeClassifier;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link DecisionTreeMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DecisionTreeMapperTest {

    @Inject
    private DecisionTreeMapper decisionTreeMapper;

    @Test
    public void testMapDecisionTree() {
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
    public void testMapChaid() {
        DecisionTreeClassifier treeClassifier = TestHelperUtils.createDecisionTreeClassifier(DecisionTreeType.CHAID);
        DecisionTreeOptions options = decisionTreeMapper.map(treeClassifier);
        Assertions.assertThat(options.getDecisionTreeType()).isEqualTo(DecisionTreeType.CHAID);
        Assertions.assertThat(options.getAdditionalOptions()).isNotEmpty();
        Assertions.assertThat(Double.valueOf(options.getAdditionalOptions().get(OptionsVariables.ALPHA))).isEqualTo(
                ((CHAID) treeClassifier).getAlpha());
    }
}
