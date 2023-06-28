package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.TestHelperUtils;
import com.ecaservice.classifier.options.model.DecisionTreeOptions;
import eca.ensemble.forests.DecisionTreeType;
import eca.trees.CHAID;
import eca.trees.DecisionTreeClassifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link DecisionTreeMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(DecisionTreeMapperImpl.class)
class DecisionTreeMapperTest {

    @Inject
    private DecisionTreeMapper decisionTreeMapper;

    @Test
    void testMapDecisionTree() {
        DecisionTreeClassifier treeClassifier = TestHelperUtils.createDecisionTreeClassifier(DecisionTreeType.C45);
        DecisionTreeOptions options = decisionTreeMapper.map(treeClassifier);
        assertThat(options.getSeed()).isEqualTo(treeClassifier.getSeed());
        assertThat(options.getNumRandomSplits()).isEqualTo(treeClassifier.getNumRandomSplits());
        assertThat(options.getMinObj()).isEqualTo(treeClassifier.getMinObj());
        assertThat(options.getMaxDepth()).isEqualTo(treeClassifier.getMaxDepth());
        assertThat(options.getNumRandomAttr()).isEqualTo(treeClassifier.getNumRandomAttr());
        assertThat(options.getRandomTree()).isEqualTo(treeClassifier.isRandomTree());
        assertThat(options.getUseBinarySplits()).isEqualTo(treeClassifier.getUseBinarySplits());
        assertThat(options.getUseRandomSplits()).isEqualTo(treeClassifier.isUseRandomSplits());
        assertThat(options.getDecisionTreeType()).isEqualTo(DecisionTreeType.C45);
    }

    @Test
    void testMapChaid() {
        DecisionTreeClassifier treeClassifier = TestHelperUtils.createDecisionTreeClassifier(DecisionTreeType.CHAID);
        DecisionTreeOptions options = decisionTreeMapper.map(treeClassifier);
        assertThat(options.getDecisionTreeType()).isEqualTo(DecisionTreeType.CHAID);
        assertThat(options.getAlpha()).isEqualTo(((CHAID) treeClassifier).getAlpha());
    }

    @Test
    void testMapDecisionTreeType() {
        for (var decisionTreeType : DecisionTreeType.values()) {
            DecisionTreeClassifier treeClassifier = TestHelperUtils.createDecisionTreeClassifier(decisionTreeType);
            DecisionTreeOptions options = decisionTreeMapper.map(treeClassifier);
            assertThat(options).isNotNull();
            assertThat(options.getDecisionTreeType()).isEqualTo(decisionTreeType);
        }
    }
}
